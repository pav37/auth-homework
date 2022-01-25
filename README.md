# Описание
Для аутентификации и авторизации используется Authentication and Authorization code flow, 
    для этого в приложение добавлен компонент oauth2-proxy (https://github.com/oauth2-proxy/oauth2-proxy) для выполнения аутентификации/авторизации. 
Таким образом аутентификация/авторизация вынесена в отдельный сервис (oauth2-proxy), который использует "внешний" сервис Auth Service
для авторизации/аутентификации пользователей.
Каждый сервис (в нашем случае это только Myapp) может проверить пришедший в запросе токен на сервере аутентификации (auth service), 

Список компонентов:
1. Api Gateway (Nginx Controller + Oauth2 proxy) - аутентификация пользователей для доступа к приложению Myapp.
2. Auth Service - сервер аутентификации/авторизации/регистрации пользователей.
3. Myapp (service) - сервис, доступ к которому имеют только зарегистрированные в Auth Service пользователи.
4. Oauth2-proxy позволяет использовать внешний сервис аутентификации/авторизации (Auth Service) для доступа к защищенным ресурсам (Myapp).

Сценарий использования аутентификации для доступа к ресурсу:
1. Пользователь запрашивает защищенный ресурс /hello на сервисе Myapp.
2. Поскольку пользователь еще не авторизован, его перенаправляет на форму логина на сервере авторизации Auth Service.
3. После ввода логина/пароля пользователя его перенаправляет с полученным Authorization code на авторизацию доступа к приложению.
4. Oauth2-proxy используя свои Client Id и Client Secret проверяет полученный Authorization code и получает токен доступа на сервере авторизации Auth Service.
5. Пользователь(браузер) используя полученный токен доступа посредством oauth2-proxy обращается к защищенному ресурсу.
6. Защищенный ресурс проверяет токен на сервере авторизации Auth Service.
7. Защищенный ресурс (/hello) возвращает ответ на запрос (в нашем случае это токен в responseBody и приветствие).


# Развертывание
    helm dependency update
    helm -n auth upgrade --install --create-namespace app .
Манифесты развертываются в namespace auth

# Проверка
Проверка регистрации/логина/логаута пользователей, просмотр и редактирование профиля пользователя
newman run UserRegistration-Auth_Arch.postman_collection.json

Простой пример регистрации/логина пользователя и доступа к ресурсу
newman run UserRegistration-Auth_Arch_Example.postman_collection.json

# Удаление развернутых ресурсов
    helm -n auth delete app 
    kubectl delete namespace auth

[Ссылка на схему](myapp-auth-oauth2-proxy.puml)
![Схема](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/pav37/auth-homework/main/myapp-auth-oauth2-proxy.puml)


Домашнее задание/проектная работа разработано(-на) для курса [Microservice Architecture](https://otus.ru/lessons/microservice-architecture)
