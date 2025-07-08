pipeline{
    agent any

    environment {
        APP_NAME = 'jedi-planner'
        APP_VERSION = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
        DOCKER_NAMESPACE = 'fajrarisqulla'
        DOCKER_IMAGE = "${DOCKER_NAMESPACE}/${APP_NAME}"
    }

    stages {
        stage('Build and Push Multi-Platform Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials',
                                                   passwordVariable: 'DOCKER_PASSWORD',
                                                   usernameVariable: 'DOCKER_USERNAME')]) {

                        sh """
                            echo "=== Docker Login ==="
                            echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin

                            echo "=== Building Multi-Platform Image ==="
                            # Build and push for multiple platforms
                            docker buildx build \\
                                --platform linux/amd64,linux/arm64/v8 \\
                                --build-arg JAR_FILE=build/libs/*.jar \\
                                --tag ${DOCKER_IMAGE}:${APP_VERSION} \\
                                --push \\
                                .

                            echo "=== Multi-platform build completed ==="
                        """

                        if (env.BRANCH_NAME == 'main') {
                            sh """
                                echo "=== Building and pushing latest tag ==="
                                docker buildx build \\
                                    --platform linux/amd64,linux/arm64/v8 \\
                                    --build-arg JAR_FILE=build/libs/*.jar \\
                                    --tag ${DOCKER_IMAGE}:latest \\
                                    --push \\
                                    .
                            """
                        } else if (env.BRANCH_NAME == 'develop') {
                            sh """
                                echo "=== Building and pushing develop tag ==="
                                docker buildx build \\
                                    --platform linux/amd64,linux/arm64/v8 \\
                                    --build-arg JAR_FILE=build/libs/*.jar \\
                                    --tag ${DOCKER_IMAGE}:develop \\
                                    --push \\
                                    .
                            """
                        }

                        sh "docker logout"
                    }
                }
            }
        }
    }

    post {
        always {
            sh """
                echo "=== Cleanup ==="
                docker buildx rm mybuilder || true
                docker system prune -f || true
            """
        }
    }
}