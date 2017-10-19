# Quoridor
## Description
Реализация настольной игры Quoridor с некоторыми новыми фичами
## Members
* @barzug - Барсуков Сергей
* @OkciD - Байков Юрий
* @EvgeniaNevtrinosova - Невтриносова Евгения
* @erm0shin - Ермошин Даниил

## API

| Действие | Метод | url | Тело запроса | Тело ответа |
| --- | --- | --- | --- | --- |
| Зарегистрироваться | Post | /signup | {"login":"user", "email":"user@mail.ru", "password":"12345"} | {"login":"user", "email":"user@mail.ru"} |
| Авторизоваться | Post | /signin | {"login":"user", "password":"12345"} | {"login":"user", "email":"user@mail.ru"} |
| Разлогиниться | Delete | /signout |  | {"info":"Successful logout"} |
| Запросить пользователя текущей сессии | Get | /currentUser |  | {"login":"user", "email":"user@mail.ru"} |
| Изменить логин пользователя текущей сессии | Path | /currentUser/changeLogin | {"login":"user"} | {"info":"Login changed"} |
| Изменить почту пользователя текущей сессии | Path | /currentUser/changeEmail | {"email":"user@mail.ru"} | {"info":"Email changed"} |
| Изменить пароль пользователя текущей сессии | Path | /currentUser/changePass | {"oldPassword":"12345", "newPassword":"67890"} | {"info":"Password changed"} |
