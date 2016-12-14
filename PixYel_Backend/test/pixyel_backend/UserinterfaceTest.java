/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import pixyel_backend.database.exceptions.UserCreationException;

/**
 *
 * @author i01frajos445
 */
public class UserinterfaceTest {

    public static void main(String[] args) throws UserCreationException {
        
    }

    /**
     * [0] = percentage [1] = free in GBs [2] = total in GBs
     *
     * @return
     */
    public static double[] getSystemMemoryUtilisation() {
        JavaSysMon mon = new JavaSysMon();
        double[] result = new double[3];
        result[1] = (double) (mon.physical().getFreeBytes() / 10000000) / 100;
        result[2] = (double) (mon.physical().getTotalBytes() / 10000000) / 100;
        result[0] = (double) (Math.round((result[1] / result[2]) * 10000)) / 100;
        return result;
    }

    public static double getCpuUtilisation() {
        JavaSysMon mon = new JavaSysMon();
        CpuTimes cpuTimes = mon.cpuTimes();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {

        }
        return mon.cpuTimes().getCpuUsage(cpuTimes);
    }

    public static double getCpuFrequency() {
        JavaSysMon mon = new JavaSysMon();
        return (double) (mon.cpuFrequencyInHz() / 10000000) / 100;
    }

}
