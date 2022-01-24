# Описание
Для аутентификации и авторизации используется Authentication and Authorization code flow, 
    для этого в приложение добавлен компонент oauth2-proxy (https://github.com/oauth2-proxy/oauth2-proxy). 

[Ссылка на схему](myapp-auth-oauth2-proxy.puml)

Список компонентов:
1. Api Gateway (Nginx Controller + Oauth2 proxy) - аутентификация пользователей для доступа к приложению Myapp.
2. Auth Service - сервер аутентификации/авторизации/регистрации пользователей.
3. Myapp (service) - сервис, доступ к которому имеют только зарегистрированные в Auth Service пользователи.
4. Oauth2-proxy позволяет использовать внешний сервис аутентификации/авторизации (Auth Service) для доступа к защищенным ресурсам (Myapp).

Сценарий использования аутентификации для доступа к ресурсу:
1. Пользователь запрашивает защищенный ресурс /hello.
2. Поскольку пользователь еще не авторизован, его перенаправляет на форму логина на сервере авторизации Auth Service.
3. После ввода логина/пароля пользователя его перенаправляет с полученным Authorization code на авторизацию доступа к приложению.
4. Oauth2-proxy используя свои Client Id и Client Secret проверяет полученный Authorization code и получает токен доступа на сервере Auth Service.
5. Пользователь(браузер) используя полученный токен доступа посредством oauth2-proxy обращается к защищенному ресурсу.
6. Защищенный ресурс (/hello) возвращает токен в responseBody и приветствие.


# Развертывание
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

Домашнее задание/проектная работа разработано(-на) для курса [Microservice Architecture](https://otus.ru/lessons/microservice-architecture)
