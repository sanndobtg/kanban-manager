FROM denoland/deno:2.1.4

WORKDIR /app

# Copie les sources
COPY . .

# Cache les dépendances
RUN deno cache main.ts

EXPOSE 3000

CMD ["deno", "run", "--allow-net", "--allow-env", "--allow-read", "main.ts"]
