pipeline {
    agent any

    tools {
        nodejs 'NodeJS-20'
    }

    stages {
        stage('AI Generate Pipeline') {
            steps {
                sh 'echo "PATH=$PATH" && which node && which npx && ln -sf $(which npx) /usr/local/bin/npx 2>/dev/null || true'
                aiAgent(
                    apiCredentialsId: 'anthropic-api-key',
                    approvalTimeoutSeconds: 1,
                    failOnAgentError: false,
                    yoloMode: true,
                    prompt: '''
请分析当前工作区的项目代码结构，然后生成一个完整的 Jenkinsfile.generated 文件。

项目已知信息：
- 构建工具：Maven（pom.xml）
- 容器化：Docker（Dockerfile）
- 部署方式：Kubernetes + Helm（helm/ 和 k8s/ 目录）

Pipeline 应包含以下阶段：
1. Checkout     - 代码检出（已由 SCM 处理，可省略或添加注释）
2. Build        - mvn clean package -DskipTests
3. Test         - mvn test（包含测试报告发布）
4. Code Analysis - mvn sonar:sonar 或 checkstyle（如有配置）
5. Docker Build  - 构建并推送 Docker 镜像（使用 BUILD_NUMBER 作为 tag）
6. Deploy        - 通过 Helm 部署到 Kubernetes

要求：
- 使用 Declarative Pipeline 语法
- 添加 post 块处理成功/失败通知
- 使用 environment 块定义镜像名称等变量
- 将生成的内容写入工作区根目录的 Jenkinsfile.generated 文件
'''
                )
            }
        }

        stage('Archive Generated Jenkinsfile') {
            steps {
                archiveArtifacts artifacts: 'Jenkinsfile.generated', allowEmptyArchive: true
                echo '已归档生成的 Jenkinsfile.generated，请查看构建产物'
            }
        }
    }

    post {
        success {
            echo '✅ AI Agent 已成功生成 Jenkinsfile，请在构建产物中查看 Jenkinsfile.generated'
        }
        failure {
            echo '❌ AI Agent 执行失败，请检查 API Key 凭据和 Agent 日志'
        }
    }
}
