package io.hpp.noosphere.gw.client;

import io.hpp.noosphere.gw.service.dto.RateLimitConfig;
import io.hpp.noosphere.gw.web.rest.dto.AgentContainerDTO;
import io.hpp.noosphere.gw.web.rest.dto.AgentDTO;
import io.hpp.noosphere.gw.web.rest.dto.AgentRequestDTO;
import io.hpp.noosphere.gw.web.rest.dto.ContainerDTO;
import io.hpp.noosphere.gw.web.rest.dto.CreateWalletRequest;
import io.hpp.noosphere.gw.web.rest.dto.UserDTO;
import io.hpp.noosphere.gw.web.rest.dto.UserSubscriptionDTO;
import io.hpp.noosphere.gw.web.rest.dto.VerifierDTO;
import io.hpp.noosphere.gw.web.rest.vm.KeepAliveResponse;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import io.hpp.noosphere.gw.web.rest.vm.RegisterAgentVm;
import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentContainerVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentRequestVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchContainerVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchUserVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchVerifierVm;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.FallbackFactory;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(
  name = "noosphere-hub",
  fallbackFactory = NoosphereHubClient.NoosphereHubClientFallbackFactory.class,
  configuration = NoosphereHubFeignConfiguration.class
)
public interface NoosphereHubClient {
  @PostMapping("/api/users/search")
  Flux<UserDTO> searchUsers(@RequestBody SearchUserVm searchVm, @SpringQueryMap PageableVm pageable);

  @GetMapping("/api/rate-limit-configs")
  Flux<RateLimitConfig> getRateLimitConfigs(@SpringQueryMap PageableVm pageable);

  @GetMapping("/api/rate-limit-configs/{key}")
  Mono<RateLimitConfig> getRateLimitConfig(@PathVariable("key") String key);

  @PutMapping("/api/rate-limit-configs/{key}")
  Mono<Void> updateRateLimitConfig(@PathVariable("key") String key, @RequestBody RateLimitConfig config);

  @PostMapping("/api/wallets")
  Mono<String> createWallet(@RequestBody CreateWalletRequest createWalletRequest);

  @PostMapping("/api/containers/search")
  Flux<ContainerDTO> searchContainers(@RequestBody SearchContainerVm searchVm, @SpringQueryMap PageableVm pageable);

  @PostMapping("/api/containers")
  Mono<ContainerDTO> createContainer(@RequestBody ContainerDTO containerDTO);

  @GetMapping("/api/containers/{id}")
  Mono<ContainerDTO> getContainer(@PathVariable("id") UUID id);

  @DeleteMapping("/api/containers/{id}")
  Mono<Void> deleteContainer(@PathVariable("id") UUID id);

  @PostMapping("/api/verifiers/search")
  Flux<VerifierDTO> searchVerifiers(@RequestBody SearchVerifierVm searchVm, @SpringQueryMap PageableVm pageable);

  @PostMapping("/api/verifiers")
  Mono<VerifierDTO> createVerifier(@RequestBody VerifierDTO verifierDTO);

  @GetMapping("/api/verifiers/{id}")
  Mono<VerifierDTO> getVerifier(@PathVariable("id") UUID id);

  @PostMapping("/api/agent-requests/search")
  Flux<AgentRequestDTO> searchAgentRequests(@RequestBody SearchAgentRequestVm searchVm, @SpringQueryMap PageableVm pageable);

  @PostMapping("/api/agent-requests")
  Mono<AgentRequestDTO> createAgentRequest(@RequestBody AgentRequestDTO agentRequestDTO);

  @GetMapping("/api/agent-requests/{id}")
  Mono<AgentRequestDTO> getAgentRequest(@PathVariable("id") UUID id);

  @PostMapping("/api/users/mine/wallet")
  Mono<String> createWallet(@RequestBody UpdateWalletVm updateWalletVm);

  @PutMapping("/api/users/mine/wallet")
  Mono<String> updateWallet(@RequestBody UpdateWalletVm updateWalletVm);

  @GetMapping("/api/users/mine/wallet")
  Mono<String> getWallet();

  @GetMapping("/api/users/mine/api-key")
  Mono<String> getApiKey();

  @PostMapping("/api/users/mine/api-key")
  Mono<String> createApiKey();

  @GetMapping("/api/users/profile")
  Mono<UserDTO> getUserProfile();

  @PutMapping("/api/users/profile")
  Mono<Void> updateUserProfile(@RequestBody UserDTO userDTO);

  @PutMapping("/api/agents/{id}")
  Mono<AgentDTO> updateAgent(@PathVariable("id") UUID id, @RequestBody AgentDTO agentDTO);

  @PostMapping("/api/agents/search")
  Flux<AgentDTO> searchAgents(@RequestBody SearchAgentVm searchVm, @SpringQueryMap PageableVm pageable);

  @GetMapping("/api/agents/{id}")
  Mono<AgentDTO> getAgent(@PathVariable("id") UUID id);

  @DeleteMapping("/api/agents/{id}")
  Mono<Void> deleteAgent(@PathVariable("id") UUID id);

  @PostMapping("/api/agents/register")
  Mono<AgentDTO> registerAgent(@RequestBody RegisterAgentVm agentVm);

  @GetMapping("/api/agents/{id}/subscriptions")
  Flux<UserSubscriptionDTO> getSubscriptions(@PathVariable("id") UUID id, @RequestParam("size") Integer size);

  @PutMapping("/api/agents/{agentId}/containers/{containerId}")
  Mono<AgentContainerDTO> createAgentContainer(@PathVariable("agentId") UUID agentId, @PathVariable("containerId") UUID containerId);

  @PostMapping("/api/agents/{agentId}/containers/search")
  Flux<AgentContainerDTO> searchAgentContainers(
    @PathVariable("agentId") UUID agentId,
    @RequestBody SearchAgentContainerVm searchVm,
    @SpringQueryMap PageableVm pageable
  );

  @GetMapping("/api/agents/{agentId}/containers/{containerId}")
  Mono<AgentContainerDTO> getAgentContainer(@PathVariable("agentId") UUID agentId, @PathVariable("containerId") UUID containerId);

  @DeleteMapping("/api/agents/{agentId}/containers/{containerId}")
  Mono<Void> deleteAgentContainer(@PathVariable("agentId") UUID agentId, @PathVariable("containerId") UUID containerId);

  @PutMapping("/api/agents/{agentId}/keep-alive")
  Mono<KeepAliveResponse> keepAlive(@PathVariable("agentId") UUID agentId);

  @GetMapping("/api/users/from-api-key")
  Mono<UserDTO> getUserFromApiKey(@RequestHeader("X-API-KEY") String apiKey);

  @Component
  class NoosphereHubClientFallbackFactory implements FallbackFactory<NoosphereHubClient> {

    private static final Logger log = LoggerFactory.getLogger(NoosphereHubClientFallbackFactory.class);

    @Override
    public NoosphereHubClient apply(Throwable cause) {
      log.error("NoosphereHubClient fallback; original cause: {}", cause.getMessage());
      return new NoosphereHubClient() {
        @Override
        public Flux<UserDTO> searchUsers(SearchUserVm searchVm, PageableVm pageable) {
          return Flux.empty();
        }

        @Override
        public Flux<RateLimitConfig> getRateLimitConfigs(PageableVm pageable) {
          return Flux.empty();
        }

        @Override
        public Mono<RateLimitConfig> getRateLimitConfig(String key) {
          return Mono.empty();
        }

        @Override
        public Mono<Void> updateRateLimitConfig(String key, RateLimitConfig config) {
          return Mono.empty();
        }

        @Override
        public Mono<String> createWallet(CreateWalletRequest createWalletRequest) {
          return Mono.empty();
        }

        @Override
        public Flux<ContainerDTO> searchContainers(SearchContainerVm searchVm, PageableVm pageable) {
          return Flux.empty();
        }

        @Override
        public Mono<ContainerDTO> createContainer(ContainerDTO containerDTO) {
          return Mono.empty();
        }

        @Override
        public Mono<ContainerDTO> getContainer(UUID id) {
          return Mono.empty();
        }

        @Override
        public Mono<Void> deleteContainer(UUID id) {
          return Mono.empty();
        }

        @Override
        public Flux<VerifierDTO> searchVerifiers(SearchVerifierVm searchVm, PageableVm pageable) {
          return Flux.empty();
        }

        @Override
        public Mono<VerifierDTO> createVerifier(VerifierDTO verifierDTO) {
          return Mono.empty();
        }

        @Override
        public Mono<VerifierDTO> getVerifier(UUID id) {
          return Mono.empty();
        }

        @Override
        public Flux<AgentRequestDTO> searchAgentRequests(SearchAgentRequestVm searchVm, PageableVm pageable) {
          return Flux.empty();
        }

        @Override
        public Mono<AgentRequestDTO> createAgentRequest(AgentRequestDTO agentRequestDTO) {
          return Mono.empty();
        }

        @Override
        public Mono<AgentRequestDTO> getAgentRequest(UUID id) {
          return Mono.empty();
        }

        @Override
        public Mono<String> createWallet(UpdateWalletVm updateWalletVm) {
          return Mono.empty();
        }

        @Override
        public Mono<String> updateWallet(UpdateWalletVm updateWalletVm) {
          return Mono.empty();
        }

        @Override
        public Mono<String> getWallet() {
          return Mono.empty();
        }

        @Override
        public Mono<String> getApiKey() {
          return Mono.empty();
        }

        @Override
        public Mono<String> createApiKey() {
          return Mono.empty();
        }

        @Override
        public Mono<UserDTO> getUserProfile() {
          return Mono.empty();
        }

        @Override
        public Mono<Void> updateUserProfile(UserDTO userDTO) {
          return Mono.empty();
        }

        @Override
        public Mono<AgentDTO> updateAgent(UUID id, AgentDTO agentDTO) {
          return Mono.empty();
        }

        @Override
        public Flux<AgentDTO> searchAgents(SearchAgentVm searchVm, PageableVm pageable) {
          return Flux.empty();
        }

        @Override
        public Mono<AgentDTO> getAgent(UUID id) {
          return Mono.empty();
        }

        @Override
        public Mono<Void> deleteAgent(UUID id) {
          return Mono.empty();
        }

        @Override
        public Mono<AgentDTO> registerAgent(RegisterAgentVm agentVm) {
          return Mono.empty();
        }

        @Override
        public Flux<UserSubscriptionDTO> getSubscriptions(UUID id, Integer size) {
          return Flux.empty();
        }

        @Override
        public Mono<AgentContainerDTO> createAgentContainer(UUID agentId, UUID containerId) {
          return Mono.empty();
        }

        @Override
        public Flux<AgentContainerDTO> searchAgentContainers(
          UUID agentId,
          SearchAgentContainerVm searchVm,
          PageableVm pageable
        ) {
          return Flux.empty();
        }

        @Override
        public Mono<AgentContainerDTO> getAgentContainer(UUID agentId, UUID containerId) {
          return Mono.empty();
        }

        @Override
        public Mono<Void> deleteAgentContainer(UUID agentId, UUID containerId) {
          return Mono.empty();
        }

        @Override
        public Mono<KeepAliveResponse> keepAlive(UUID agentId) {
          return Mono.empty();
        }

        @Override
        public Mono<UserDTO> getUserFromApiKey(String apiKey) {
          return Mono.empty();
        }
      };
    }
  }
}
