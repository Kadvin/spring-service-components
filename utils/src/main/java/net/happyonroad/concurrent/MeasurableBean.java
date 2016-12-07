package net.happyonroad.concurrent;

import net.happyonroad.spring.ApplicationSupportBean;
import net.happyonroad.type.TimeInterval;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

/**
 * <h1>可以被做性能评估的bean</h1>
 *
 * @author Jay Xiong
 */
public class MeasurableBean extends ApplicationSupportBean {
    private Measurement measurement = new Measurement();

    @Override
    protected void performStart() {
        super.performStart();
        this.startMeasure();
    }

    protected void startMeasure(){
        measurement.start();
    }

    protected void measuring(){
        measurement.prepare();
    }

    protected void measured(int size){
        measurement.increase(size);
    }

    @ManagedOperation(description = "重置统计数据")
    public void resetMeasure(){
        measurement.reset();
    }

    @ManagedAttribute(description = "每秒速度")
    public String getSpeedPerSecond(){
        return String.format("%.2f 个/秒", measurement.getSpeed());
    }

    @ManagedAttribute(description = "每分钟速度")
    public String getSpeedPerMinute(){
        return String.format("%.2f 个/分钟", measurement.getSpeed() * 60);
    }

    @ManagedAttribute(description = "每千个消息实际耗时")
    public String getAvgCost(){
        return String.format("%.2f 毫秒/千个", measurement.getAvgCost());
    }

    @ManagedAttribute(description = "总处理个数")
    public Long getTotal(){
        return measurement.getTotal();
    }

    @ManagedAttribute(description = "总处理耗时")
    public String getCost(){
        return TimeInterval.parse(measurement.getCost());
    }

}
