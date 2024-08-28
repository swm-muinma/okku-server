# 빌드 스테이지: node:18-alpine 이미지를 사용하여 빌드
FROM node:18-alpine AS build

# 작업 디렉토리를 설정합니다.
WORKDIR /usr/src/app

# package.json과 package-lock.json을 복사합니다.
COPY package*.json ./

# 의존성을 설치합니다.
RUN npm install

# 프로젝트의 모든 파일을 작업 디렉토리로 복사합니다.
COPY . .

# TypeScript를 컴파일합니다.
RUN npm run build

# 런타임 스테이지: 빌드된 파일만 사용하여 새로운 이미지 생성
FROM node:18-alpine

# 작업 디렉토리를 설정합니다.
WORKDIR /usr/src/app

# 빌드된 파일만 복사합니다.
COPY --from=build /usr/src/app/dist ./dist
COPY --from=build /usr/src/app/package*.json ./
COPY --from=build /usr/src/app/.env .env
# COPY AuthKey_7NN2NV7FA6.p8 /usr/src/app/AuthKey_7NN2NV7FA6.p8
# RUN chmod 600 /usr/src/app/AuthKey_7NN2NV7FA6.p8

# 의존성을 설치합니다 (production 모드로 설치)
RUN npm install --only=production

# 애플리케이션을 시작합니다.
CMD ["node", "dist/app.js"]
