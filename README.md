# CustomHorizontal

사용하기 쉬운 라이브러리를 연습으로 만들어봤습니다.  
https://github.com/Mulham-Raee/Horizontal-Calendar 여기 코드를 참고하였습니다

build.gradle(Project:)

    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }

build.gradle(Module:)

    dependencies {
        implementation 'com.github.hanjm93:CustomHorzontal:1.0.2'
    }
