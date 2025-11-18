package io.hpp.noosphere.gw.client;

import io.hpp.noosphere.gw.web.rest.dto.AgentRequestDTO;
import io.hpp.noosphere.gw.web.rest.dto.ContainerDTO;
import io.hpp.noosphere.gw.web.rest.dto.CreateWalletRequest;
import io.hpp.noosphere.gw.web.rest.dto.UserDTO;
import io.hpp.noosphere.gw.web.rest.dto.VerifierDTO;
import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentRequestVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchContainerVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchVerifierVm;
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
import reactivefeign.FallbackFactory;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "noosphere-hub", fallbackFactory = NoosphereHubClient.NoosphereHubClientFallbackFactory.class)
public interface NoosphereHubClient {
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

  @Component
  class NoosphereHubClientFallbackFactory implements FallbackFactory<NoosphereHubClient> {

    private static final Logger log = LoggerFactory.getLogger(NoosphereHubClientFallbackFactory.class);

    @Override
    public NoosphereHubClient apply(Throwable cause) {
      log.error("NoosphereHubClient fallback; original cause: {}", cause.getMessage());
      return new NoosphereHubClient() {
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
      };
    }
  }
}