pipeline{
    agent {
        docker {
            image 'docker:25.0.0-cli'  // Latest Docker with buildx included
            args '-v /var/run/docker.sock:/var/run/docker.sock --privileged'
        }
    }

    environment {
        APP_NAME = 'jedi-planner'
        APP_VERSION = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
        DOCKER_NAMESPACE = 'fajrarisqulla'
        DOCKER_IMAGE = "${DOCKER_NAMESPACE}/${APP_NAME}"
        DOCKER_BUILDKIT = '1'  // Enable BuildKit
    }

    stages {
        stage('Install and Setup Buildx') {
            steps {
                script {
                    sh """
                        echo "=== Installing Docker Buildx ==="
                        # Check Docker version
                        docker version

                        # Download and install buildx plugin
                        BUILDX_VERSION=v0.12.1
                        wget -O docker-buildx "https://github.com/docker/buildx/releases/download/\${BUILDX_VERSION}/buildx-\${BUILDX_VERSION}.linux-amd64"
                        chmod +x docker-buildx
                        mkdir -p ~/.docker/cli-plugins
                        mv docker-buildx ~/.docker/cli-plugins/

                        # Verify installation
                        docker buildx version

                        echo "=== Setting up QEMU for multi-platform ==="
                        docker run --rm --privileged multiarch/qemu-user-static --reset -p yes

                        echo "=== Creating builder ==="
                        docker buildx create --name multibuilder --driver docker-container --use
                        docker buildx inspect --bootstrap

                        echo "=== Available platforms ==="
                        docker buildx ls
                    """
                }
            }
        }

        stage('Setup Multi-platform Builder') {
            steps {
                script {
                    sh """
                        echo "=== Setting up Docker Buildx ==="
                        # Check if buildx is available
                        docker buildx version

                        # Create a new builder instance for multi-platform builds
                        docker buildx create --name mybuilder --use --bootstrap || true

                        # Verify the builder supports multiple platforms
                        docker buildx inspect --bootstrap

                        echo "=== Available platforms ==="
                        docker buildx ls
                    """
                }
            }
        }

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