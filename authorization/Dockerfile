FROM java:8
VOLUME /tmp
ADD /target/**.jar cim-auth.jar
RUN bash -c 'touch /cim-auth.jar'
ENTRYPOINT ["java","-jar","/cim-auth.jar"]
