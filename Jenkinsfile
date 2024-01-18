pipeline{
	agent any
		tools{
	        maven "maven 3.5.0"
	    }
	stages{
    stage('Start Project'){
	    steps{
checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: '13daaff0-994d-486d-8d1b-0f050daeeb26', url: 'https://github.com/ciimst/event-map-docker-simple-web.git']])		    
	    sh 'bash Script.sh -r localhost:5000'
	    //sh 'helm uninstall event-map-chart'
	    sh 'helm install event-map-chart ./event-map-helm-chart'
	    }
	}

  }
  }
