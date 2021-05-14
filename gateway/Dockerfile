FROM java:8
VOLUME /tmp
ADD /target/**.jar cim-gateway.jar
RUN bash -c 'touch /cim-gateway.jar'
ENTRYPOINT ["java","-jar","/cim-gateway.jar"]
