# =============================================================================
# Runner do Gitea Actions com as ferramentas do curso ja' instaladas (Aula 26)
#
# E' o mesmo act_runner oficial, acrescido de JDK 17 E 21 (a matriz de versoes
# escolhe qual usar), Maven, Python, Node, Bruno CLI e o cliente Docker. Os jobs
# rodam DENTRO deste container (modo "host" do act_runner): nada de baixar JDK
# a cada execucao, e o cache do Maven e do pip fica no volume /root. O cliente
# Docker fala com o Docker da sua maquina pelo docker.sock - e' assim que o
# pipeline gera imagens e sobe os ambientes de staging e producao.
# =============================================================================
FROM gitea/act_runner:0.6.1

RUN apk add --no-cache \
        bash curl git socat \
        openjdk17-jdk openjdk21-jdk maven \
        python3 py3-pip \
        nodejs npm \
        docker-cli \
    && npm install -g @usebruno/cli@4.3.0 \
    && npm cache clean --force

# Os workflows escolhem o JDK com JAVA_HOME=/usr/lib/jvm/java-<versao>-openjdk
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/sbin/tini", "--", "entrypoint.sh"]
