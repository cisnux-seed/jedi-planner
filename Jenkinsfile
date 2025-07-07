pipeline{
    agent any

    environment {
        APP_NAME = 'jedi-planner'
        APP_VERSION = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_NAMESPACE = 'fajrarisqulla'
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/${APP_NAME}"
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                        def customImage = docker.build("${DOCKER_IMAGE}:${APP_VERSION}",
                            "--build-arg JAR_FILE=build/libs/*.jar .")
                        customImage.push()

                        if (env.GIT_BRANCH == 'main') {
                            customImage.push('latest')
                        } else if (env.GIT_BRANCH == 'develop') {
                            customImage.push('develop')
                        }
                    }
                }
            }
        }
    }
}