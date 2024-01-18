pipeline{
	agent any
		tools{
	        maven "maven 3.5.0"
	    }
	stages{
    stage('Start Project'){
	    steps{
checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: '13daaff0-994d-486d-8d1b-0f050daeeb26', url: 'https://github.com/ciimst/event-map-docker-simple.git']])		    
	    sh 'bash Script.sh -r localhost:5000'
	    sh 'helm uninstall event-map-chart'
	    sh 'helm install event-map-chart ./event-map-helm-chart'
	    }
	}
		
    stage('Build docker image for load kube image'){
      steps{
        script{
		sh 'docker build -t event_map_admin -f DockerfileBased .'
		sh 'docker build -t event_map_admin -f DockerfileAdmin .'
		sh 'docker images'
        }
      }

	post {
	failure {
		mail bcc: '', body: '''Docker permission denied hatası olabilir. sudo chmod 666 /var/run/docker.sock komut satırı ile izinleri açmanız gerekmektedir.!!!!
                Thanks, Ayse''', cc: '', from: '', replyTo: '', subject: 'Docker failed', to: 'aysayparcasi@gmail.com'
                echo 'e-mail OK!'

	}
    }
    }

  }
  }
