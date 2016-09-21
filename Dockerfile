FROM java:8
RUN apt-get update && apt-get install -y \
  git-core \
  git-svn \
  subversion

#The effect is to create a temporary file on your host under "/var/lib/docker" and link it to the container under "/tmp"

RUN mkdir -p /etc/gsync

VOLUME /tmp

ADD gsync-0.1-SNAPSHOT.jar /etc/gsync/app.jar

CMD java -jar /etc/gsync/app.jar