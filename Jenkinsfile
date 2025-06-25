pipeline {
	agent any

    environment {
		OPENSHIFT_SERVER = credentials('openshift-server-url')
        OPENSHIFT_TOKEN = credentials('openshift-token')
        OPENSHIFT_PROJECT = 'jedi-planner'
        APP_NAME = 'jedi-planner'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    tools {
		gradle 'Gradle-8.5'
        jdk 'JDK-'
    }

    stages {
		stage('Build & Unit Test') {
			steps {
				echo 'Building Spring Boot R2DBC GraphQL application with Gradle...'
                sh '''
                    ./gradlew clean build -x test
                    ./gradlew test --tests "*"
                '''
            }
            post {
				always {
					publishTestResults testResultsPattern: 'build/test-results/test/*.xml'
                    archiveArtifacts artifacts: 'build/libs/*.jar'
                }
            }
        }

        stage('Build Image') {
			steps {
				echo 'Building container image...'
                sh """
                    oc login ${OPENSHIFT_SERVER} --token=${OPENSHIFT_TOKEN} --insecure-skip-tls-verify=true
                    oc project ${OPENSHIFT_PROJECT}

                    # Create BuildConfig if not exists
                    oc new-build --name=${APP_NAME} --binary=true --strategy=docker || echo "BuildConfig already exists"

                    # Build image from current directory
                    oc start-build ${APP_NAME} --from-dir=. --follow --wait
                    oc tag ${APP_NAME}:latest ${APP_NAME}:${IMAGE_TAG}
                """
            }
        }

        stage('Deploy to OpenShift') {
			steps {
				echo 'Deploying to OpenShift...'
                sh """
                    # Apply all Kubernetes manifests
                    oc apply -f k8s/

                    # Update deployment with new image
                    if oc get deployment ${APP_NAME}; then
                        oc set image deployment/${APP_NAME} ${APP_NAME}=${APP_NAME}:${IMAGE_TAG}
                    else
                        oc new-app ${APP_NAME}:${IMAGE_TAG} --name=${APP_NAME}
                        oc expose svc/${APP_NAME} || echo "Route already exists"
                    fi

                    # Wait for deployment to complete
                    oc rollout status deployment/${APP_NAME} --timeout=300s

                    # Show deployment info
                    oc get pods -l app=${APP_NAME}
                    oc get route ${APP_NAME} || echo "No route found"
                """
            }
        }
    }

    post {
		always {
			echo 'Cleaning up...'
            sh 'oc logout || true'
            cleanWs()
        }
        success {
			echo '✅ Pipeline completed successfully!'
        }
        failure {
			echo '❌ Pipeline failed!'
        }
    }
}