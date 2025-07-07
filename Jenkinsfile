pipeline{
    agent any

    environment {
        APP_NAME = 'jedi-planner'
        APP_VERSION = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
        DOCKER_NAMESPACE = 'fajrarisqulla'
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_IMAGE = "${DOCKER_NAMESPACE}/${APP_NAME}"
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                        def customImage = docker.build(
                            "${DOCKER_IMAGE}:${APP_VERSION}",
                            "--build-arg JAR_FILE=build/libs/*.jar ."
                        )
                        customImage.push()
                        if (env.GIT_BRANCH == 'main') {
                            customImage.push('latest')
                        } else if (env.GIT_BRANCH == 'develop') {
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