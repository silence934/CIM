FROM java:8
VOLUME /tmp
ADD /target/**.jar cim-socket.jar
RUN bash -c 'touch /cim-socket.jar'
ENTRYPOINT ["java","-jar","/cim-socket.jar"]
