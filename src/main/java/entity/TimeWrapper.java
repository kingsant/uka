package entity;


import cn.hutool.core.date.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TimeWrapper {

    private List<TimeNode> timeline;
    private String code;
    private Double percentage;
    private Long now;

    public TimeWrapper(String code, Double percentage, Long now) {
        this.code = code;
        this.percentage = percentage;
        this.now = now;
        TimeNode tn = new TimeNode();
        tn.setTime(now);
        tn.setPercentage(percentage);
        timeline = new ArrayList<TimeNode>();
        timeline.add(tn);
    }

    public List<TimeNode> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimeNode> timeline) {
        this.timeline = timeline;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Long getNow() {
        return now;
    }

    public void setNow(Long now) {
        this.now = now;
    }

    public Double addAndCheck(Double percentage, Long time) {
        this.percentage = percentage;
        this.now = time;
        //先清理数据

        cleanOldDate(timeline);
        if (CollectionUtils.isEmpty(timeline)) {
            TimeNode tn = new TimeNode();
            tn.setTime(time);
            tn.setPercentage(percentage);
            timeline.add(tn);
        }

        TimeNode timeNode = timeline.get(0);
        return percentage - timeNode.getPercentage();
    }

    private void cleanOldDate(List<TimeNode> timeline) {
        //当前时间
        String yyyyMMddHHmmss = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        Long l = Long.valueOf(yyyyMMddHHmmss);
        //todo 1分钟写死
        Long threshold = l - 60;
        Iterator<TimeNode> iterator = timeline.iterator();
        while (iterator.hasNext()) {
            TimeNode tn = iterator.next();
            if (tn.getTime() < threshold) {
                iterator.remove();
            }
        }
    }
}
