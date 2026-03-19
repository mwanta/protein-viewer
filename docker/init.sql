SELECT 'CREATE DATABASE proteinviewer'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'proteinviewer')\gexec