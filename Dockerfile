FROM python:3.12-slim
WORKDIR /app
COPY . /app
RUN if [ -f requirements.txt ]; then pip install --no-cache-dir -r requirements.txt; fi
COPY docker/entrypoint.sh /usr/local/bin/repo-entrypoint
RUN chmod +x /usr/local/bin/repo-entrypoint && useradd -m -u 10001 appuser && chown -R appuser:appuser /app
USER appuser
EXPOSE 8000
ENTRYPOINT ["/usr/local/bin/repo-entrypoint"]
