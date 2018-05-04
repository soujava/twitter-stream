# Pull base image.
FROM openjdk:8-jdk

MAINTAINER  <ivensdeveloper@gmail.com>

# update dpkg repositories
RUN apt-get update

# install wget
RUN apt-get install -y wget

RUN mkdir /opt/maven
RUN mkdir /opt/java-exercise


# download maven
RUN wget --no-verbose -O /tmp/apache-maven-3.0.5.tar.gz http://archive.apache.org/dist/maven/maven-3/3.0.5/binaries/apache-maven-3.0.5-bin.tar.gz

# install maven
RUN tar -C /opt/maven -xzf /tmp/apache-maven-3.0.5.tar.gz --strip-components=1
ENV MAVEN_HOME=/opt/maven
ENV MAVEN_OPTS="-Xmx1048m -Xms256m -XX:MaxPermSize=312M"
ENV PATH $MAVEN_HOME/bin:$PATH

COPY . /opt/java-exercise

WORKDIR /opt/java-exercise
RUN mvn clean install


CMD ["mvn"]