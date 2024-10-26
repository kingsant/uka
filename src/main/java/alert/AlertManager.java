package alert;

import bean.StockBean;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.intellij.ide.util.PropertiesComponent;
import com.jgoodies.common.base.Strings;
import common.Consts;
import notice.NoticeManager;
import org.apache.commons.lang.time.DateUtils;

import java.util.concurrent.TimeUnit;

/**
 * 主要用来 告警指标计算
 */
public class AlertManager {

    // 存个2分钟的数据，假如当前数据1分钟内，涨跌2cm告警
    public static void recordPriceAndAlert(StockBean bean) {
        boolean needAlert = PropertiesComponent.getInstance().getBoolean(Consts.KEY_ALERT);
        if (!needAlert) {
            return;
        }
        if (Strings.isBlank(bean.getTime())) {
            return;
        }
        String res = PriceTimelineManager.addAndCheck(bean);
        if (Strings.isBlank(res)) {
            return;
        }
        NoticeManager.notify("uka", res);
    }
}
