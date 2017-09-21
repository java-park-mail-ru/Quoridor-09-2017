# Quoridor
***
## Description
Реализация настольной игры Quoridor с некоторыми новыми фичами
***
## Members
* @barzug - Барсуков Сергей
* @OkciD - Байков Юрий
* @EvgeniaNevtrinosova - Невтриносова Евгения
* @erm0shin - Ермошин Даниил

## API

| Действие | url | Тело запроса | Тело ответа |
| --- | --- | --- | --- |
| Зарегистрироваться | /signup | {"login":"user", "email":"user@mail.ru", "password":"12345"} | {"login":"user", "email":"user@mail.ru"} |
| Авторизоваться | /signin | {"login":"user", "password":"12345"} | {"login":"user", "email":"user@mail.ru"} |
| Разлогиниться | /signout |  | {"info":"Successful logout"} |
| Запросить пользователя текущей сессии | /currentUser |  | {"login":"user", "email":"user@mail.ru"} |
| Изменить логин пользователя текущей сессии | /currentUser/changeLogin | {"login":"user"} | {"info":"Login changed"} |
| Изменить почту пользователя текущей сессии | /currentUser/changeEmail | {"email":"user@mail.ru"} | {"info":"Email changed"} |
| Изменить пароль пользователя текущей сессии | /currentUser/changePass | {"oldPassword":"12345", "newPassword":"67890"} | {"info":"Password changed"} |
