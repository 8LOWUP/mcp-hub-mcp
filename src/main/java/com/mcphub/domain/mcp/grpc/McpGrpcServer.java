package com.mcphub.domain.mcp.grpc;

import com.mcphub.domain.mcp.repository.jsp.UserMcpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
public class McpGrpcServer extends McpServiceGrpc.McpServiceImplBase {

    private final UserMcpRepository userMcpRepository;

    @Override
    @Transactional(readOnly = true)
    public void getMcpUrlTokenPairs(McpUrlTokenRequest request,
                                    StreamObserver<McpUrlTokenResponse> responseObserver) {

        Long userId = Long.parseLong(request.getUserId());
        List<Long> mcpIds = request.getMcpIdsList();

        // DB에서 MCP URL, Token 조회
        List<McpUrlTokenPair> pairs = userMcpRepository.findAllByUserIdAndMcpIds(userId, mcpIds).stream()
                .map(userMcp -> McpUrlTokenPair.newBuilder()
                        .setUrl(userMcp.getMcp().getRequestUrl())
                        .setToken(userMcp.getPlatformToken() == null ? "" : userMcp.getPlatformToken())
                        .build())
                .collect(Collectors.toList());

        McpUrlTokenResponse response = McpUrlTokenResponse.newBuilder()
                .addAllPairs(pairs)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
