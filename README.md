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

HorizontalCalendar를 사용할 class 에서는 다음과 같이 선언할 수 있습니다

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val defaultSelectedDate = Calendar.getInstance()

        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)

        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 1)

        val builder = HorizontalCalendar.Builder(this, R.id.calendarView)
            .range(startDate, endDate)
            .datesNumberOnScreen(7)
            .defaultSelectedDate(defaultSelectedDate)
            .configure()
            .formatTopText("MMM")
            .formatMiddleText("dd")
            .formatBottomText("EEE")
            .showTopText(true)
            .showBottomText(true)
            .textColor(Color.LTGRAY, Color.WHITE)
            .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
            .end()

        builder.build().calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                
            }
        }
    }
