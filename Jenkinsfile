pipeline{
    agent any

    environment {
        APP_NAME = 'jedi-planner'
        APP_VERSION = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
        DOCKER_NAMESPACE = 'fajrarisqulla'
        DOCKER_IMAGE = "${DOCKER_NAMESPACE}/${APP_NAME}"
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials',
                                                   passwordVariable: 'DOCKER_PASSWORD',
                                                   usernameVariable: 'DOCKER_USERNAME')]) {

                        sh """
                            echo "=== Authenticating with Docker Hub ==="
                            echo docker login -u \$DOCKER_USERNAME --password-stdin
                            echo "Authentication successful"
                        """

                        def customImage = docker.build("${DOCKER_IMAGE}:${APP_VERSION}",
                                                      "--build-arg JAR_FILE=build/libs/*.jar .")

                        customImage.push()

                        if (env.BRANCH_NAME == 'main') {
                            customImage.push('latest')
                        } else if (env.BRANCH_NAME == 'develop') {
                            customImage.push('develop')
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
                docker rmi ${DOCKER_IMAGE}:${APP_VERSION} || true
                if [ "${env.BRANCH_NAME}" = "main" ]; then
                    docker rmi ${DOCKER_IMAGE}:latest || true
                elif [ "${env.BRANCH_NAME}" = "develop" ]; then
                    docker rmi ${DOCKER_IMAGE}:develop || true
                fi
            """
        }
    }
}