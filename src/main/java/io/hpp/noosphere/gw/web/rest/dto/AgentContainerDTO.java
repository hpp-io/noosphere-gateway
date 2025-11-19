package io.hpp.noosphere.gw.web.rest.dto;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A DTO for the {@link io.hpp.noosphere.hub.domain.AgentContainer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentContainerDTO implements Serializable {

    private UUID id;

    private StatusCode statusCode;

    private Instant createdAt;

    private Instant updatedAt;

    private AgentDTO agent;

    private ContainerDTO container;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AgentContainerDTO that)) {
            return false;
        }

        return new EqualsBuilder().append(id, that.id).append(agent, that.agent).append(container, that.container).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(agent).append(container).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("statusCode", statusCode)
            .append("createdAt", createdAt)
            .append("updatedAt", updatedAt)
            .append("agent", agent != null ? agent.getId() : null)
            .append("container", container != null ? container.getId() : null)
            .toString();
    }
}
