package com.imooc.grocers.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.imooc.grocers.dal.ShopModelMapper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CanalScheduling implements Runnable, ApplicationContextAware {

    private ApplicationContext applicationContext; //用于获取bean

    @Resource
    private CanalConnector canalConnector; //CanalClient返回的bean

    @Autowired
    private ShopModelMapper shopModelMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    @Scheduled(fixedDelay = 100) //每100ms会执行相应线程, 监听DB binlog变化
    public void run() {
        long batchId = -1;
        try {
            int batchSize = 1000;
            Message message = canalConnector.getWithoutAck(batchSize);
            batchId = message.getId();
            List<CanalEntry.Entry> entries = message.getEntries(); //获得指定条数的数据变化
            if (batchId != -1 && entries.size() > 0) {
                entries.forEach(entry -> {
                    if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                        //解析处理
                        publishCanalEvent(entry);
                    }
                });
            }
            canalConnector.ack(batchId);
        } catch (Exception e) {
            e.printStackTrace();
            canalConnector.rollback(batchId);
        }
    }

    private void publishCanalEvent(CanalEntry.Entry entry) {
        CanalEntry.EventType eventType = entry.getHeader().getEventType(); //update DB is a kind of eventType
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        CanalEntry.RowChange change = null;
        try {
            change = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return;
        }
        //可能会影响所有行数据
        change.getRowDatasList().forEach(rowData -> {
            List<CanalEntry.Column> columns = rowData.getAfterColumnsList();
            String primaryKey = "id";
            // 1. column是unique index
            // 2. name is "id"
            CanalEntry.Column idColumn = columns.stream().filter(column -> column.getIsKey()
                    && primaryKey.equals(column.getName())).findFirst().orElse(null);
            Map<String, Object> dataMap = parseColumnsToMap(columns);
            try {
                indexES(dataMap, database, table);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 返回Map中Key对象column名字,value为Object
     * @param columns
     * @return
     */
    private Map<String, Object> parseColumnsToMap(List<CanalEntry.Column> columns) {
        Map<String, Object> jsonMap = new HashMap<>();
        columns.forEach(column -> {
            if (column == null) {
                return;
            }
            jsonMap.put(column.getName(), column.getValue());
        });
        return jsonMap;
    }

    // table change -> canal -> canal client -> fetch updated joined table by changed ID -> update ES index
    private void indexES(Map<String, Object> dataMap, String database, String table) throws IOException {
        if (!StringUtils.equals("grocers", database)) {
            return;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        if (StringUtils.equals("seller", table)) {
            result = shopModelMapper.buildESQuery(new Integer((String)dataMap.get("id")), null, null);
        } else if (StringUtils.equals("category", table)) {
            result = shopModelMapper.buildESQuery(null, new Integer((String)dataMap.get("id")), null);
        } else if (StringUtils.equals("shop", table)) {
            result = shopModelMapper.buildESQuery(null, null, new Integer((String)dataMap.get("id")));
        } else {
            return;
        }

        for (Map<String, Object> map : result) {
            IndexRequest indexRequest = new IndexRequest("shop");
            indexRequest.id(String.valueOf(map.get("id")));
            indexRequest.source(map);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
