FROM java:8
VOLUME /tmp
ADD /target/**.jar cim-api.jar
RUN bash -c 'touch /cim-api.jar'
ENTRYPOINT ["java","-jar","/cim-api.jar"]
