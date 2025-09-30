@Configuration
@Profile("dev") // dev 환경에서만 활성화
public class SentryDevConfig {

    @PostConstruct
    public void init() {
        Sentry.init(options -> {
            // application.yml (공통 설정: dsn, sample rate 등) + 여기서 override 가능
            options.setEnvironment("mcp-service"); // 서비스별 환경 이름
        });
    }
}
