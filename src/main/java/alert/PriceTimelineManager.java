package alert;

import bean.StockBean;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import entity.TimeNode;
import entity.TimeWrapper;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PriceTimelineManager {

    static Cache<String, TimeWrapper> codeCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();


    public static String addAndCheck(StockBean bean) {
        //20241025155912
        String code = bean.getCode();
        Double percentage = Double.valueOf(bean.getChangePercent());
        Long time = Long.valueOf(bean.getTime());
        //up to | low to
        TimeWrapper wrapper = codeCache.getIfPresent(code);
        if (wrapper == null) {
            wrapper = new TimeWrapper(code, percentage, time);
            codeCache.put(code, wrapper);
            return null;
        } else {
            //不是null
           Double distance =  wrapper.addAndCheck(percentage, time);
           if (Math.abs(distance) < 2) {
               return null;
           }
           return code + " -> " + percentage  + "(" + distance + ")";
        }

    }

    public static void cleanTimeline(String code) {
        TimeWrapper wrapper = codeCache.getIfPresent(code);
        if (wrapper != null) {
            wrapper.setTimeline(new ArrayList<>());
        }
    }
}
