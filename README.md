# Lab 10 — Definition–Use хосууд

**Оюутан:** Б. Энхбаяр  
**Оюутны код:** B232270018

Орчны гаралт (энэ ажлыг гүйцэтгэх үед):

```text
$ java -version
openjdk version "17.0.20.1" 2026-08-18
OpenJDK Runtime Environment Temurin-17.0.20.1+1 (build 17.0.20.1+1)
OpenJDK 64-Bit Server VM Temurin-17.0.20.1+1 (build 17.0.20.1+1, mixed mode, sharing)

$ mvn -version
Apache Maven 3.9.15 (98b2cdbfdb5f1ac8781f537ea9acccaed7922349)
Maven home: C:\Program Files\apache-maven-3.9.15
Java version: 21.0.5, vendor: Eclipse Adoptium, runtime: C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot
Default locale: en_US, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

## A. `gcd(x, y)` DU анализ

Доорх мөрийн дугаар нь `DataFlowTargets.java` файлын дугаар. `T/F` нь predicate-use-ийн салааг тэмдэглэв. Замын товч бичлэг `15-T → 16 → 17 → 18 → 15` гэх мэтээр гүйцэтгэсэн мөрүүдийг заана.

| Хувьсагч | Def мөр | Use мөр | Төрөл | Def-clear зам | Тестийн ID | Хамрагдсан |
|---|---:|---:|---|---|---|---|
| x | 13 | 16 | c-use | `13 → 15-T → 16` | `gcdOneLoopIteration`, `gcdMultipleLoopIterations` | Тийм |
| x | 13 | 20 | c-use | `13 → 15-F → 20` (давталтгүй зам) | `gcdZeroSecondArgument` | Тийм |
| x | 17 | 16 | c-use | `17 → 18 → 15-T → 16` | `gcdMultipleLoopIterations` | Тийм |
| x | 17 | 20 | c-use | `17 → 18 → 15-F → 20` | `gcdOneLoopIteration`, `gcdMultipleLoopIterations` | Тийм |
| y | 13 | 15 | p-use, T | `13 → 15-T` | `gcdOneLoopIteration`, `gcdMultipleLoopIterations` | Тийм |
| y | 13 | 15 | p-use, F | `13 → 15-F` | `gcdZeroSecondArgument` | Тийм |
| y | 13 | 16 | c-use | `13 → 15-T → 16` | `gcdOneLoopIteration`, `gcdMultipleLoopIterations` | Тийм |
| y | 13 | 17 | c-use | `13 → 15-T → 16 → 17` | `gcdOneLoopIteration`, `gcdMultipleLoopIterations` | Тийм |
| y | 18 | 15 | p-use, T | `18 → 15-T` (дараагийн давталтын шалгалт) | `gcdMultipleLoopIterations` | Тийм |
| y | 18 | 15 | p-use, F | `18 → 15-F` (давталт дуусах шалгалт) | `gcdOneLoopIteration`, `gcdMultipleLoopIterations` | Тийм |
| y | 18 | 16 | c-use | `18 → 15-T → 16` | `gcdMultipleLoopIterations` | Тийм |
| y | 18 | 17 | c-use | `18 → 15-T → 16 → 17` | `gcdMultipleLoopIterations` | Тийм |
| tmp | 16 | 18 | c-use | `16 → 17 → 18` | `gcdOneLoopIteration`, `gcdMultipleLoopIterations` | Тийм |

`gcd(7, 0)` ганцаараа давталтгүй `x`-ийн 13-р мөрийн тодорхойлолтоос 20-р мөрийн use хүртэлх хосыг, мөн `y`-ийн 13-р мөрийн тодорхойлолтоос 15-р мөрийн худал салааг хамарна. `gcd(12, 8)` хоёр давталт хийж, 18-р мөрөнд тодорхойлогдсон `y` ба 17-р мөрөнд тодорхойлогдсон `x` дараагийн давталтад use-д хүрэх хосуудыг хамарна. Тиймээс эхний давталттай кейс болон олон давталттай кейс хоёулаа шаардлагатай.

## B. `bonus(attendance, threshold)`

### B4. Эхний шат — all-defs

`BonusTest.allDefsResetAndNoBonus` болон `BonusTest.allDefsPresentAndStreakBonus` хоёр тест бүх definition-ийг дор хаяж нэг хэрэглээнд хүргэнэ. Хоёр тестээс цөөн байж болохгүй: 50-р мөрийн `score = 0` тодорхойлолт хэрэглэгдэхийн тулд 51-р мөрийн нөхцөл худал байх ёстой; 51-р мөрийн `score = 5` тодорхойлолтыг үүсгэхийн тулд мөн нөхцөл үнэн байх ёстой. Нэг гүйцэтгэлд энэ нөхцөл хоёр өөр утгатай байх боломжгүй. Эхний тест 46-р мөрийн `streak = 0` тодорхойлолтыг дараагийн эерэг ирцийн мөчлөгийн 42-р мөрийн нөхцөлд хүргэнэ. Хоёр дахь тест 43-р мөрийн `best` тодорхойлолт, 53-р мөрийн `score` тодорхойлолтуудыг use-д хүргэнэ.

`results/mvn-verify-alldefs.txt`-д нийт 5 тест амжилттай (GCD 3, bonus all-defs 2) гарсан. `results/jacoco-alldefs.csv` дахь `DataFlowTargets` мөр: Branch 14/16, Line 23/25 (1 далд constructor болон 1 оролтын алдааны мөр хамрагдаагүй). Иймд all-defs хангагдсан ч branch 100% биш байж болно.

### B1–B3. CFG ба бүрэн DU шинжилгээ

Хийгдэж байна.

## C. Fault seeding

Хийгдэж байна.

## Хэрэглэсэн тусламж

AI туслахыг зааврын дагуу энэ ажлын хүрээнд код, тест, DU хосын анализ болон тайлан боловсруулахад ашиглав. Бүх тест, гаралтын файлыг төслийн орчинд ажиллуулж шалгасан.
