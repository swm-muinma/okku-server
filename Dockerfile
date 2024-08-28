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
# COPY AuthKey_7NN2NV7FA6.p8 /usr/src/app/AuthKey_7NN2NV7FA6.p8
# RUN chmod 600 /usr/src/app/AuthKey_7NN2NV7FA6.p8

# 환경 변수 설정
ENV AUTH_KEY_PATH="/usr/src/app/AuthKey_7NN2NV7FA6.p8"

# 의존성을 설치합니다 (production 모드로 설치)
RUN npm install --only=production

# 환경 변수를 설정합니다.
ENV MONGODB_URI_MAIN=$MONGODB_URI_MAIN
ENV MONGODB_URI_REVIEW_INSIGHT=$MONGODB_URI_REVIEW_INSIGHT
ENV MONGODB_URI_REVIEW=$MONGODB_URI_REVIEW

ENV JWT_SECRET=$JWT_SECRET
ENV EXPIRATION_TIME=$EXPIRATION_TIME
ENV AUTH_KEY_PATH=$AUTH_KEY_PATH
ENV REFRESHTOKEN_EXPIRATION_TIME=$REFRESHTOKEN_EXPIRATION_TIME
# OAUTH
ENV BASE_REDIRECT_URI=$BASE_REDIRECT_URI

# BASE_REDIRECT_URI="http://localhost:3000/login/oauth2/code/"
## OAUTH_APPLE
ENV APPLE_CLIENT_ID=$APPLE_CLIENT_ID
ENV APPLE_TEAM_ID=$APPLE_TEAM_ID
ENV APPLE_KEY_ID=$APPLE_KEY_ID
ENV APPLE_SCOPE=$APPLE_SCOPE
ENV APPLE_KEY_FILE_PATH=$APPLE_KEY_FILE_PATH
## OAUTH_KAKAO
ENV KAKAO_CLIENT_ID=$KAKAO_CLIENT_ID

ENV SCRAPER_URL=$SCRAPER_URL

# 애플리케이션을 시작합니다.
CMD ["node", "dist/app.js"]
