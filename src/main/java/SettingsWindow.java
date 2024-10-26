import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.text.Strings;
import common.Consts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import quartz.QuartzManager;
import utils.HttpClientPool;
import utils.LogUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsWindow  implements Configurable {
    private JPanel panel1;
    private JTextArea textAreaStock;
    private JCheckBox checkbox;
    /**
     * 使用tab界面，方便不同的设置分开进行控制
     */
    private JTabbedPane tabbedPane1;
    private JCheckBox checkBoxTableStriped;
    private JTextField cronExpressionStock;
    private JCheckBox checkboxSina;
    private JCheckBox checkboxLog;
    private JLabel proxyLabel;
    private JTextField inputProxy;
    private JButton proxyTestButton;
    private JCheckBox alertCheckBox;

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "uka";
    }

    @Override
    public @Nullable JComponent createComponent() {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        String value_stock = instance.getValue("key_stocks");
        boolean value_color = instance.getBoolean("key_colorful");
        textAreaStock.setText(value_stock);
        checkbox.setSelected(!value_color);
        checkBoxTableStriped.setSelected(instance.getBoolean("key_table_striped"));
        checkboxSina.setSelected(instance.getBoolean("key_stocks_sina"));
        checkboxLog.setSelected(instance.getBoolean("key_close_log"));
        alertCheckBox.setSelected(instance.getBoolean(Consts.KEY_ALERT));
        cronExpressionStock.setText(instance.getValue("key_cron_expression_stock","*/10 * * * * ?")); //默认每10秒执行
        //代理设置
        inputProxy.setText(instance.getValue("key_proxy"));
        proxyTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String proxy = inputProxy.getText().trim();
                testProxy(proxy);
            }
        });
        return panel1;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        String errorMsg = checkConfig();
        if (Strings.isNotEmpty(errorMsg)) {
            throw new ConfigurationException(errorMsg);
        }
        PropertiesComponent instance = PropertiesComponent.getInstance();
        instance.setValue("key_stocks", textAreaStock.getText());
        instance.setValue("key_colorful",!checkbox.isSelected());
        instance.setValue("key_cron_expression_stock", cronExpressionStock.getText());
        instance.setValue("key_table_striped", checkBoxTableStriped.isSelected());
        instance.setValue("key_stocks_sina",checkboxSina.isSelected());
        instance.setValue("key_close_log",checkboxLog.isSelected());
        instance.setValue(Consts.KEY_ALERT,alertCheckBox.isSelected());
        String proxy = inputProxy.getText().trim();
        instance.setValue("key_proxy",proxy);
        HttpClientPool.getHttpClient().buildHttpClient(proxy);
        StockWindow.apply();
    }


    private void testProxy(String proxy){
        if (proxy.indexOf('：')>0){
            LogUtil.notify("别用中文分割符啊!",false);
            return;
        }
        HttpClientPool httpClientPool = HttpClientPool.getHttpClient();
        httpClientPool.buildHttpClient(proxy);
        try {
            httpClientPool.get("https://www.baidu.com");
            LogUtil.notify("代理测试成功!请保存",true);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.notify("测试代理异常!",false);
        }
    }

    public static List<String> getConfigList(String key, String split) {
        String value = PropertiesComponent.getInstance().getValue(key);
        if (Strings.isEmpty(value)) {
            return new ArrayList<>();
        }
        Set<String> set = new LinkedHashSet<>();
        String[] codes = value.split(split);
        for (String code : codes) {
            if (!code.isEmpty()) {
                set.add(code.trim());
            }
        }
        return new ArrayList<>(set);
    }

    public static List<String> getConfigList(String key) {
        String value = PropertiesComponent.getInstance().getValue(key);
        if (Strings.isEmpty(value)) {
            return new ArrayList<>();
        }
        Set<String> set = new LinkedHashSet<>();
        String[] codes = null;
        if (value.contains(";")) {//包含分号
            codes = value.split("[;]");
        } else {
            codes = value.split("[,，]");
        }
        for (String code : codes) {
            if (!code.isEmpty()) {
                set.add(code.trim());
            }
        }
        return new ArrayList<>(set);
    }

    /**
     * 检查配置项
     *
     * @return 返回提示的错误信息
     */
    private String checkConfig() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append(getConfigList(cronExpressionStock.getText(), ";").stream().map(s -> {
            if (!QuartzManager.checkCronExpression(s)) {
                return "Stock请配置正确的cron表达式[" + s + "]、";
            } else {
                return "";
            }
        }).collect(Collectors.joining()));

        return errorMsg.toString();
    }
}
