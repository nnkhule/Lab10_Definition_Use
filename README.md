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

`BonusTest.resetNoBonus` болон `BonusTest.presentStreakBonus` хоёр тест бүх definition-ийг дор хаяж нэг хэрэглээнд хүргэнэ. Хоёр тестээс цөөн байж болохгүй: 49-р мөрийн `score = 0` тодорхойлолт хэрэглэгдэхийн тулд 50-р мөрийн нөхцөл худал байх ёстой; 51-р мөрийн `score = 5` тодорхойлолтыг үүсгэхийн тулд мөн нөхцөл үнэн байх ёстой. Нэг гүйцэтгэлд энэ нөхцөл хоёр өөр утгатай байх боломжгүй. Эхний тест 46-р мөрийн `streak = 0` тодорхойлолтыг дараагийн эерэг ирцийн мөчлөгийн 41-р мөрийн c-use-д хүргэнэ. Хоёр дахь тест 43-р мөрийн `best` тодорхойлолт, 54-р мөрийн `score` тодорхойлолтуудыг use-д хүргэнэ.

`results/mvn-verify-alldefs.txt`-д нийт 5 тест амжилттай (GCD 3, bonus all-defs 2) гарсан. `results/jacoco-alldefs.csv` дахь `DataFlowTargets` мөр: Branch 14/16, Line 23/25 (7-р мөрийн далд constructor болон 33-р мөрийн exception statement хамрагдаагүй). Иймд all-defs хангагдсан ч branch 100% биш байж болно.

Бүрэн DU тестийн шатанд `results/mvn-verify-final.txt` нийт 12 тест амжилттай, `results/jacoco-final.csv` Branch 16/16, Line 24/25 гэж харуулна. Branch 100% нь DU хамралтыг батлахгүй: `gcd(7,0)` болон `gcd(0,5)` нь `while (y != 0)`-ийн T, F салбар хоёуланг хамруулна, гэхдээ олон давталтын `x`-ийн 17-р мөрөөс 16-р мөр рүү болон `y`-ийн 18-р мөрөөс 16/17-р мөрийн use-д хүрэх DU хосуудыг хамруулахгүй. `gcd(12,8)` шиг олон давталттай тест тэдгээрийг шаарддаг. Иймээс branch тоо шийдвэр бүрийн салаа гүйцэтгэснийг хэлдэг бол DU хүснэгт тодорхой definition-ээс use хүртэлх def-clear замыг шалгана.

### B1. Control-flow graph

![bonus()-ийн мөрийн дугаартай CFG; ирмэгт бодит тестийн методын нэрүүд байна](cfg-bonus.png)

38-р мөрийн for-each нөхцөл нь `attendance`-ийн p-use: дараагийн элемент байвал 38-р мөрд `a` хувьсагчид онооно, үгүй бол давталтаас гарна. Графын ирмэг дээр тухайн шилжилтийг гаргасан бодит тестийн методыг бичсэн.

### B2. Definition/use жагсаалт

| Хувьсагч | Def мөр | Use мөр ба төрөл |
|---|---|---|
| `attendance` | 31 (параметр) | 32 p-use (`attendance == null`); 38 p-use (дараагийн элемент байгаа эсэх) |
| `threshold` | 31 (параметр) | 32 p-use (`threshold < 0`, эхний нөхцөл худал үед); 50 p-use (`present >= threshold`) |
| `present` | 35, 40 | 40 c-use (өөрийн баруун тал); 50 p-use |
| `streak` | 36, 41, 46 | 41 c-use (өөрийн баруун тал); 42 p-use |
| `best` | 37, 43 | 42 p-use; 53 p-use |
| `a` | 38 (for-each-ийн оноолт) | 39 p-use (`a > 0`) |
| `score` | 49, 51, 54 | 54 c-use (өөрийн баруун тал); 56 c-use |

Definition дахин оноогдоход хуучин утга kill болно:

| Хувьсагчийн def | Kill хийх дараагийн def | Тайлбар |
|---|---|---|
| `attendance` 31 | байхгүй | Параметрийн local def дахин оноогдохгүй. |
| `threshold` 31 | байхгүй | Параметрийн local def дахин оноогдохгүй. |
| `present` 35 | 40 | Эхний эерэг өдөр өмнөх утгыг дарна. |
| `present` 40 | дараагийн 40 | Дараагийн эерэг өдөр өмнөх def-ийг дарна. |
| `streak` 36 | 41 эсвэл 46 | 41 нэмэгдүүлнэ; 46 тасалсан өдөр тэглэнэ. 46 нь эхний 36-р мөрийн def-ийг ч kill хийнэ. |
| `streak` 41 | дараагийн 41 эсвэл 46 | Дараагийн эерэг өдрийн increment эсвэл тасалсан өдрийн reset хуучин def-ийг дарна. |
| `streak` 46 | дараагийн 41 эсвэл 46 | Дараагийн эерэг өдрийн increment эсвэл дахин reset хуучин def-ийг дарна. |
| `best` 37, 43 | 43 | `streak > best` үнэн үед шинэ best хуучин def-ийг дарна. |
| `a` 38 | дараагийн 38 | For-each дараагийн элементийг онооно. |
| `score` 49 | 51 эсвэл 54 | 51-ийн assignment kill хийнэ; 54-ийн RHS эхлээд хуучин утгыг use хийгээд дараа нь def үүсгэнэ. |
| `score` 51 | 54 | 54-ийн RHS 51-ийн утгыг use хийгээд дараа нь шинэ def үүсгэнэ. |
| `score` 54 | байхгүй | Нэмэгдүүлэлтээс хойш буцах def байхгүй. |

### B3. Def-clear DU хосууд

Замын тэмдэглэгээнд зөвхөн DU-д хамаарах мөрүүдийг харуулав; `38-T` нь дараагийн ирцийн элемент байгаа, `38-F` нь давталт дууссаныг илтгэнэ. Мөр 32-ын `attendance` болон `threshold` p-use нь short-circuit нөхцөл тул тусдаа хувьсагчийн шалгалтыг ялгаж тэмдэглэв.

| Хувьсагч | Def | Use | Төрөл/салаа | Def-clear зам | Тестийн ID |
|---|---:|---:|---|---|---|
| attendance | 31 | 32 | p-use, null=T | `31 → 32-T` | `nullAttendance` |
| attendance | 31 | 32 | p-use, null=F | `31 → 32-F` | `resetNoBonus` |
| attendance | 31 | 38 | p-use, T | `31 → 32-F → 35 → 38-T` | `resetNoBonus` |
| attendance | 31 | 38 | p-use, F | `31 → 32-F → 35 → 38-F` | `emptyArrayZeroThreshold` |
| threshold | 31 | 32 | p-use, `<0`=T | `31 → 32` (attendance null биш) | `negativeThreshold` |
| threshold | 31 | 32 | p-use, `<0`=F | `31 → 32-F` | `resetNoBonus` |
| threshold | 31 | 50 | p-use, T | `31 → 32-F → 35 → 38 → 49 → 50-T` | `emptyArrayZeroThreshold`, `resetThenFive` |
| threshold | 31 | 50 | p-use, F | `31 → 32-F → 35 → 38 → 49 → 50-F` | `resetNoBonus`, `shortResetUnderThreshold` |
| present | 35 | 40 | c-use | `35 → 38-T → 39-T → 40` | `resetNoBonus` |
| present | 35 | 50 | p-use, T | `35 → 38-F → 49 → 50-T` | `emptyArrayZeroThreshold` |
| present | 35 | 50 | p-use, F | `35 → 38-F → 49 → 50-F` | `resetNoBonus` |
| present | 40 | 40 | c-use | `40 → 38-T → 39-T → 40` | `presentStreakBonus` |
| present | 40 | 50 | p-use, T | `40 → 38-F → 49 → 50-T` | `resetThenFive` |
| present | 40 | 50 | p-use, F | `40 → 38-F → 49 → 50-F` | `shortResetUnderThreshold` |
| streak | 36 | 41 | c-use | `36 → 38-T → 39-T → 41` | `presentStreakBonus` |
| streak | 41 | 41 | c-use | `41 → 42 → 38-T → 39-T → 41` | `presentStreakBonus` |
| streak | 41 | 42 | p-use, T | `41 → 42-T` | `presentStreakBonus` |
| streak | 41 | 42 | p-use, F | `41 → 42-F` (шинэ streak өмнөх best-ээс их биш) | `resetNoBonus` |
| streak | 46 | 41 | c-use | `46 → 38-T → 39-T → 41` | `shortResetUnderThreshold` |
| best | 37 | 42 | p-use, T | `37 → 38-T → 39-T → 41 → 42-T` | `presentStreakBonus` |
| best | 37 | 53 | p-use, F | `37 → 38-F → 49 → 50 → 53-F` | `emptyArrayZeroThreshold` |
| best | 43 | 42 | p-use, T | `43 → 38-T → 39-T → 41 → 42-T` | `resetThenFive` |
| best | 43 | 42 | p-use, F | `43 → 38-T → 39-T → 41 → 42-F` | `resetThenFive` |
| best | 43 | 53 | p-use, T | `43 → 38-F → 49 → 50 → 53-T` | `resetThenFive` |
| best | 43 | 53 | p-use, F | `43 → 38-F → 49 → 50 → 53-F` | `resetNoBonus` |
| a | 38 | 39 | p-use, T | `38-T → 39-T` | `presentStreakBonus` |
| a | 38 | 39 | p-use, F | `38-T → 39-F` | `shortResetUnderThreshold` |
| score | 49 | 54 | c-use | `49 → 50-F → 53-T → 54` | `longStreakUnderThreshold` |
| score | 49 | 56 | c-use | `49 → 50-F → 53-F → 56` | `resetNoBonus` |
| score | 51 | 54 | c-use | `51 → 53-T → 54` | `presentStreakBonus` |
| score | 51 | 56 | c-use | `51 → 53-F → 56` | `emptyArrayZeroThreshold` |
| score | 54 | 56 | c-use | `54 → 56` | `presentStreakBonus` |

Kill хүснэгтэд 46-р мөрийн reset нь эхний 36-р мөрийн `streak` def болон өмнөх 41-р мөрийн def-ийг дарж болохыг тэмдэглэв. 46-аас хойш эерэг өдөр ирвэл 41-р мөр эхлээд reset утгыг RHS-ийн c-use-д уншаад шинэ def үүсгэнэ. 46-р мөрөөс 42-р мөр хүртэл def-clear зам байхгүй: 42 хүрэх эерэг өдөр гарахад 41-р мөр түрүүлж `streak`-ийг дахин тодорхойлно.

Def-clear замгүй тул DU гэж тооцохгүй хосууд: 36-р мөрийн `streak=0`-оос 42-T/F, 46-р мөрийн `streak=0`-оос 42-T/F хосуудад ямар ч зам байхгүй; 42-р мөр хүрэх эерэг өдрөөс өмнө 41-р мөр `streak`-ийг дахин тодорхойлно. 37-р мөрийн `best=0`-оос 42-F ч биелэхгүй: эхний эерэг өдөр `streak=1` тул 42 үнэн болж 43-р мөр `best`-ийг шинэчилдэг. 37-р мөрийн def-ээс 53-T хүрэх зам мөн үгүй: эерэг өдөр байвал 43-р мөрөөр kill хийгдэнэ, эерэг өдөр байхгүй бол best=0 хэвээр тул 53-F болно. 49-р мөрийн `score=0`-оос 56-р мөр хүртэл 50-T салаагаар явахад 51-р мөр kill хийнэ; 51-р мөрийн def-ээс 56 хүртэл 53-T салаагаар явахад 54-р мөр c-use хийсний дараа kill хийнэ. Тэдгээр def-clear хослол биш боловч тус бүр өөр салаанд def-clear DU хостой. Харин 46-р мөрийн def-ээс 41-р мөрийн c-use хүртэлх хос биелэх боломжтой.

## C. Fault seeding

| № | Мутаци | Үр дүн | Унасан тест / гаралтын мөр |
|---|---|---|---|
| M1 | 41-р мөрийн `streak = streak + 1`-ийг устгав (missing update) | 11 тестээс 3 унасан | `presentStreakBonus`: expected `<8>` but was `<5>`; `longStreakUnderThreshold`: expected `<3>` but was `<0>`; `resetThenFive`: expected `<8>` but was `<5>` (`results/mvn-test-M1.txt`) |
| M2 | 43-р мөрийн `best = streak`-ийг `best = present` болгосон (wrong variable) | Эхний all-DU-pairs-ийн 11 тест амжилттай, мутаци амьд үлдсэн (`results/mvn-test-M2.txt`) | Дараах тоон харьцуулалтаар шалтгааныг тайлбарлав. |
| M3 | Давталтын дараа ашиглагддаггүй `int total = present + best;` нэмсэн (dead definition) | 12 тест амжилттай, runtime тест илрүүлээгүй (`results/mvn-test-M3.txt`) | VS Code-д суулгасан Red Hat Java extension-ийн JDT compiler-аар M3 хувилбарт `-warn:+unusedLocal` шалгалт хийхэд бодит гаралт `The value of the local variable total is not used` гарсан. Бүтэн гаралт `results/m3-ide-warning.txt`-д байна. `javac` энэ анхааруулгыг гаргаагүй. |

M2 анхны 11 тестийг яагаад давсныг `best` шинэчлэгдэх мөчийн утгуудаар харьцуулав:

| Анхны тест | Шинэ def үүсэх үеийн `(present, streak, өмнөх best)` | Зөв/мутацитай эцсийн үр дүн |
|---|---|---|
| `resetNoBonus` | Эхэндээ `(1,1,0)`; reset-ийн дараа `(2,1,1)` нь `streak > best` нөхцөлд хүрэхгүй | `best=1` аль алинд; оноо 0 |
| `presentStreakBonus` | Залгаа өдрүүдэд `present == streak`, утгууд 1–5 | `best=5` аль алинд; оноо 8 |
| `shortResetUnderThreshold` | Reset-ийн дараа `(3,1,2)`, шинэчлэлт болохгүй | `best=2` аль алинд; оноо 0 |
| `resetThenFive` | Reset-ийн дараа `(5,3,2)` үед мутаци `best=5` болгоно | Дараагийн өдрүүдэд зөв streak 5 хүрдэг тул эцсийн best 5 болж таарна; оноо 8 аль алинд |
| `longStreakUnderThreshold` | Залгаа таван өдөр `present == streak` | `best=5` аль алинд; оноо 3 |
| `emptyArrayZeroThreshold`, `nullAttendance`, `negativeThreshold` | best update үүсэхгүй; сүүлийн хоёр нь exception-оор дуусна | Үр дүн ижил |

`m2KillCase` нь `[1,1,0,1,1,1]`, `threshold=5` өгнө. Сүүлийн өдөр `(present, streak, өмнөх best)=(5,3,2)`, тэгэхээр зөв код best-ийг 3 болгоно, мутаци 5 болгоно. Threshold-ын оноо 5 боловч зөв streak бонус авахгүй тул гаралт 5; мутацитай код буруу бонус нэмээд 8 буцаана. Тест expected `<5>` but was `<8>` гэж унасан (`results/mvn-test-M2-kill.txt`). Maven-ийн `Finished at` мөрүүдээр файлууд M1 23:00:15 → M2 survived 23:00:21 → M2 kill 23:00:41 → M3 23:00:47 (+08) дарааллаар үүссэн.

Мутаци турших үед `DataFlowTargets.java`-г шууд өөрчилж, туршилт бүрийн дараа `git restore` хийсэн. Эцсийн `src/main` нь Starter commit-тэй ижил.

## Хэрэглэсэн тусламж

Codex AI-г `GcdTest`, `BonusTest` болон оролтын жишээнүүдийг боловсруулахад, DU/kill хүснэгтийг гаргахад, Graphviz CFG болон README-ийн эхний хувилбарыг бэлтгэхэд ашиглав. Би хүснэгтийг эх кодын мөр ба тестийн замтай тулгаж, мутац бүрийг Maven-аар шалгаж, VS Code-ийн Red Hat Java extension-д багтсан JDT compiler-ийн M3 анхааруулгыг хадгалж, шинэ GitHub clone дээр `mvn verify`-г давтан ажиллуулсан.

## Дүгнэлт

DU анализ нь зөвхөн салаа гүйцэтгэсэн эсэхийг биш, тодорхой утга оноосон цэгээс тухайн утгыг уншсан цэг хүртэл замд дахин оноолт байхгүй эсэхийг шалгадаг. Тиймээс branch хамралт бүрэн байсан ч олон давталтаар дамжих DU хос дутуу үлдэж болдог. Харин DU шинжилгээний өртөг нь хувьсагч бүрийн def/use, kill болон def-clear замыг гараар тооцож, хүснэгтийг мөрийн дугаартай уялдуулахад оршино. Fault seeding нь DU хүснэгт бүрэн байсан ч бодит өгөгдлийн утгууд тест алдааг илрүүлэхэд чухал гэдгийг харууллаа. M1 энгийн үр дүнгийн тестүүдээр баригдсан; M2-д бүх DU хос хамрагдсан ч тодорхой нэмэлт кейс шаардлагатай байв. M3 нь динамик тест бус IDE-ийн статик анализын зорилтот асуудал юм. Хамгийн их цаг DU хосын бүх def-clear замыг нягталж, хүснэгт болон бодит тесттэй тааруулахад зарцуулагдсан.
