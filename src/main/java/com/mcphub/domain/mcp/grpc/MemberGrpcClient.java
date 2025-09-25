package com.mcphub.domain.mcp.grpc;

import com.mcphub.domain.member.grpc.GetMemberInfoRequest;
import com.mcphub.domain.member.grpc.MemberResponse;
import com.mcphub.domain.member.grpc.MemberServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class MemberGrpcClient {

	@GrpcClient("memberService")
	private MemberServiceGrpc.MemberServiceBlockingStub stub;

	public String getUserName(Long userId) {
		GetMemberInfoRequest req = GetMemberInfoRequest.newBuilder()
		                                               .setUserId(userId)
		                                               .build();

		MemberResponse resp = stub.getMemberInfo(req);
		return resp.getName();
	}
}
