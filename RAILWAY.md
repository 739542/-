# Railway Deployment

This project can be deployed to Railway directly from GitHub using the root `Dockerfile`.

## 1. Create services

Create one Railway project with these services:

- app service from this GitHub repo
- MySQL service
- Redis service

## 2. Configure the app service

In the app service:

- Source: connect the GitHub repository
- Builder: Dockerfile
- Generate Domain
- Healthcheck Path: `/health`

The application already supports Railway's injected variables:

- `PORT`
- `MYSQLHOST`
- `MYSQLPORT`
- `MYSQLUSER`
- `MYSQLPASSWORD`
- `MYSQLDATABASE`
- `REDISHOST`
- `REDISPORT`
- `REDISPASSWORD`

Only these variables still need to be set manually if you use email features:

- `MAIL_HOST`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

Optional:

- `FILE_BASE_PATH=/opt/campus-market/images/`
- `JPA_DDL_AUTO=update`
- `JPA_SHOW_SQL=false`
- `THYMELEAF_CACHE=true`

## 3. Import initial MySQL data

Railway's MySQL service will not automatically run the local `db_secondhandtrade.sql` file from this repository.

Use one of these approaches:

- Preferred: connect to the Railway MySQL service through its TCP proxy and import `db_secondhandtrade.sql` once.
- Alternative: let JPA create tables first, then manually seed only the required initial data.

## 4. Persistent files

This app stores uploaded images under:

- `/opt/campus-market/images/userImage/`
- `/opt/campus-market/images/carouselImage/`
- `/opt/campus-market/images/articleImage/`

If you need uploads to survive redeployments, attach a Railway Volume to the app service and mount it at:

- `/opt/campus-market/images`

## 5. Deploy

After the GitHub repo is connected, each push to `main` will trigger a new deployment.
