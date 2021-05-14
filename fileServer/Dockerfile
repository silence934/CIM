FROM java:8
VOLUME /tmp
ADD /target/**.jar cim-file-server.jar
RUN bash -c 'touch /cim-file-server.jar'
ENTRYPOINT ["java","-jar","/cim-file-server.jar"]
