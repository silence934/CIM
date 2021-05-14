FROM java:8
VOLUME /tmp
ADD /target/**.jar start.jar
RUN bash -c 'touch /start.jar'
ENTRYPOINT ["java","-jar","/start.jar"]
