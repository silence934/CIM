FROM java:8
VOLUME /tmp
ADD /target/**.jar cim-mail.jar
RUN bash -c 'touch /cim-mail.jar'
ENTRYPOINT ["java","-jar","/cim-mail.jar"]
