# cicd-demo

一个用来学习 **Mini DevOps 全链路** 的最小 Java 服务（Spring Boot 3.3 + JDK 21）。

```
git push → CI 构建 → 镜像打包 → 推到 Registry → K8s 部署 → 监控 + 日志
```

## 1. 本地运行

### 构建

因为系统 Maven 过旧（3.3.9 不支持 Spring Boot 3），项目自带了 Maven 3.9.9：

```bash
.tools/apache-maven-3.9.9/bin/mvn.cmd -DskipTests package
```

或升级系统 Maven 后直接：

```bash
mvn package
```

产物：`target/app.jar`

### 启动

```bash
java -jar target/app.jar
```

### 验证

| URL | 用途 |
|-----|------|
| `http://localhost:8080/hello` | 业务接口 |
| `http://localhost:8080/actuator/health` | K8s liveness/readiness 探针 |
| `http://localhost:8080/actuator/prometheus` | Prometheus 指标 |

## 2. Docker

```bash
docker build -t cicd-demo:local .
docker run --rm -p 8080:8080 cicd-demo:local
```

Dockerfile 采用两阶段构建，运行镜像基于 `eclipse-temurin:21-jre-alpine`。

## 3. CI (GitHub Actions)

推到 `main` 分支自动触发：

1. `mvn test` —— 运行单元测试
2. `mvn package` —— 打 jar
3. `docker build` —— 构建镜像
4. `docker push ghcr.io/<owner>/cicd-demo:<sha>` —— 推到 GHCR
5. （可选）SSH/kubectl 部署到 K8s

详见 `.github/workflows/ci.yml`。

## 4. K8s 部署

```bash
kubectl apply -f k8s/
```

或使用 Helm：

```bash
helm upgrade --install cicd-demo ./helm/cicd-demo
```

## 5. 可观测性

```bash
cd monitoring
docker compose up -d
```

Grafana: http://localhost:3000 （账号 admin/admin）
Prometheus: http://localhost:9090

## 目录结构

```
cicdDemo/
├── src/                       业务代码
├── pom.xml                    Maven 构建
├── Dockerfile                 镜像构建
├── .github/workflows/ci.yml   GitHub Actions
├── k8s/                       K8s 原生清单
├── helm/cicd-demo/            Helm chart
├── monitoring/                Prometheus + Grafana docker-compose
└── .tools/                    本地 Maven 副本（gitignored）
```
