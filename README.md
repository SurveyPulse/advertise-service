````markdown
# MSA 기반 SurveyPulse 광고 서비스

SurveyPulse 플랫폼의 광고(Advertisement) 관리를 담당하는 마이크로서비스입니다.
광고 생성, 조회, 수정, 삭제 및 랜덤 조회 기능을 제공합니다.

## 주요 기능

- **광고 조회 (단건)** (`GET /api/advertisements/{advertisementId}`)
  - 광고 ID로 단일 광고 조회

- **광고 목록 조회** (`GET /api/advertisements`)
  - 모든 광고 목록을 Flux 형태로 스트리밍 반환

- **광고 생성** (`POST /api/advertisements`)
  - Multipart/Form-Data로 광고 정보 및 이미지 파일 전송
  - 이미지가 있을 경우 S3에 업로드 후 URL 저장
  - Reactor Mono로 생성 결과 반환

- **광고 수정** (`PUT /api/advertisements/{advertisementId}`)
  - Multipart/Form-Data로 수정된 광고 정보 및 이미지 파일 전송
  - 이미지 변경 시 S3에 새 파일 업로드, 기존 파일 삭제

- **광고 삭제** (`DELETE /api/advertisements/{advertisementId}`)
  - 광고 엔티티 및 S3 이미지 삭제

- **랜덤 광고 조회** (`GET /api/advertisements/random?count={count}`)
  - 활성(시작일~종료일) 광고를 조회 후 가중치 기반 랜덤 샘플링
  - Redis(ElastiCache) 캐싱(TTL 1분) 적용

## 기술 스펙

- **언어 & 프레임워크**: Java, Spring Boot, Spring WebFlux
- **데이터베이스**: Spring Data Reactive Repository , Mongodb(Mongodb Atlas)
- **파일 저장소**: AWS S3 (AmazonS3 SDK)
- **캐싱**: Reactive Redis (AWS ElastiCache)
- **보안**: Spring Security, JWT
- **로깅 & 모니터링**: Elasticsearch, Logstash, Kibana (ELK), Prometheus, Grafana
- **CI/CD**: GitHub Actions
- **컨테이너 & 오케스트레이션**: Docker, Kubernetes, Helm, AWS EKS
- **아키텍처**: 마이크로서비스 아키텍처(MSA)

