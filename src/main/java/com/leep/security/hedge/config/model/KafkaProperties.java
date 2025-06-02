package com.leep.security.hedge.config.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hedge.kafka")
public class KafkaProperties {

    private boolean enabled = false;
    private String bootstrapServers = "localhost:9092";
    private Topics topics = new Topics();

    public static class Topics {
        private String sqlInjection = "api.sql";
        private String lengthControl = "api.length";
        private String rateLimit = "api.ratelimit";

        public String getSqlInjection() {
            return sqlInjection;
        }

        public void setSqlInjection(String sqlInjection) {
            this.sqlInjection = sqlInjection;
        }

        public String getLengthControl() {
            return lengthControl;
        }

        public void setLengthControl(String lengthControl) {
            this.lengthControl = lengthControl;
        }

        public String getRateLimit() {
            return rateLimit;
        }

        public void setRateLimit(String rateLimit) {
            this.rateLimit = rateLimit;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Topics getTopics() {
        return topics;
    }

    public void setTopics(Topics topics) {
        this.topics = topics;
    }
}