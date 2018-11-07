package com.epam.jvmmagic;

import com.epam.jvmmagic.CacheImpl;

import java.util.Arrays;
import java.util.Objects;

public class MyApi {

    private static boolean isInitialized;
    private static final CacheImpl CACHE = CacheImpl.getInstance();

    public static void init(String appName, String profile) {
        if (isInitialized) {
            throw new IllegalStateException("Already initialized");
        }
        //initialize static state here
    }

    public static Response request(String id) {
        return CACHE.getOrCreate(id, key -> new Response(Statuses.statusOK(), key + ": OK", new byte[]{}));
    }

    public static class Statuses {

        static int statusOK() {
            return 0;
        }
    }

    public static class Response {

        private final int status;
        private final String statusDescription;
        private final byte[] payload;

        Response(int status, String statusDescription, byte[] payload) {
            this.status = status;
            this.statusDescription = statusDescription;
            this.payload = payload;
        }

        public int getStatus() {
            return status;
        }

        public String getStatusDescription() {
            return statusDescription;
        }

        public byte[] getPayload() {
            return payload;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Response response = (Response) o;
            return status == response.status &&
                   Objects.equals(statusDescription, response.statusDescription) &&
                   Arrays.equals(payload, response.payload);
        }

        @Override
        public int hashCode() {

            int result = Objects.hash(status, statusDescription);
            result = 31 * result + Arrays.hashCode(payload);
            return result;
        }

        @Override
        public String toString() {
            return "Response{" +
                   "status=" + status +
                   ", statusDescription='" + statusDescription + '\'' +
                   ", payload=" + Arrays.toString(payload) +
                   '}';
        }
    }
}
