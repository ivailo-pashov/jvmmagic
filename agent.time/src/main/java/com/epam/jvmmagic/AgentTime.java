package com.epam.jvmmagic;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class AgentTime {

    public static void main(String[] args) {
        ByteBuddyAgent.install();
        System.out.println(ByteBuddyAgent.getInstrumentation().isModifiableClass(System.class));
        long fixedValue = LocalDate.of(2010, 3, 5)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();
        System.out.println("Using fixed value system time " + fixedValue);

        new ByteBuddy()
                .redefine(System.class)
                .method(named("currentTimeMillis"))
                .intercept(FixedValue.value(fixedValue))
                .make()
                .load(System.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        System.out.println(SimpleDateFormat.getInstance().format(new Date()));
    }
}
