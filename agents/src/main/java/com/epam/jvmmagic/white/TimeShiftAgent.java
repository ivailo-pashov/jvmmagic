package com.epam.jvmmagic.white;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;

import java.text.SimpleDateFormat;
import java.util.Date;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class TimeShiftAgent {

    private static final long BACK_IN_TIME = 822355200000L;

    public static void main(String[] args) throws InterruptedException {
        ByteBuddyAgent.install();

        new ByteBuddy()
                .redefine(System.class)
                .method(named("currentTimeMillis"))
                .intercept(FixedValue.value(BACK_IN_TIME))
                .make()
                .load(System.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        printCurrentDateTime();
        Thread.sleep(2_000);
        printCurrentDateTime();
    }

    private static void printCurrentDateTime() {
        Date now = new Date();
        System.out.println(SimpleDateFormat.getDateTimeInstance().format(now));
    }
}
