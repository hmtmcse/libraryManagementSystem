package grails.tutorial.lms


class MemberService {

    public static Integer borrowNumber = 3
    public static Integer borrowExpiry = 15


    def save(def params) {
        Member memberInstance = new Member(params)
        memberInstance.save(flush: true)
        if (memberInstance.hasErrors()) {
            memberInstance.errors.each {
                println(it)
            }
            return false
        } else {
            return true
        }
    }


    def update(def params){
        Member memberInstance = Member.get(params.id)
        if (memberInstance == null) {
            return false;
        }
        memberInstance.properties = params
        if (memberInstance.hasErrors()) {
            return false;
        }
        memberInstance.save(flush: true)
        if (memberInstance.hasErrors()) {
            memberInstance.errors.each {
                println(it)
            }
            return false
        } else {
            return true
        }
    }

    def isExistMember(String roll ){
        def isExist = Member.findByRoll(roll);
        if (isExist != null && isExist.id){
            return isExist
        }else{
            return  null
        }
    }

    def loginMe(def params){
        def isExist = Member.findByRollAndPassword(params.roll,params.password);
        if (isExist != null && isExist.id){
            return isExist
        }else{
            return  null
        }
    }

    def addToBorrowList(def params){
        def map = [:]
        map.success = false
        if (params != null && params.memberID && params.bookID){
            Book book = Book.get(params.bookID);
            Member member = Member.get(params.memberID)

            if (member != null){
                def borrow = BorrowBook.createCriteria().list {
                    and {
                        eq("member",member)
                        eq("isReturn",false)
                    }
                };
                if (borrow.size() >= 3){
                    map.message = "Borrow Maximum Limit Cross"
                    return map
                }
            }else{
                map.message = "Invalid Request"
                return map
            }


            if (book != null){
                BorrowBook borrowBook = new BorrowBook();
                borrowBook.member = member;
                borrowBook.book = book
                borrowBook.borrowDate = new Date();
                borrowBook.save(flush: true);
                if (borrowBook.hasErrors()){
                    map.message = "Can't Add to Borrow List"
                    return map
                }else{
                    map.success = true
                    return map
                }
            }else{
                map.message = "Invalid Request"
                return map
            }
        }else{
            map.message = "Invalid Request"
          return map
        }
    }

    def getBorrowListByMemberId(Integer memberID){
        def map = [:]
        map.success = false
        if (memberID != null){
            Member member = Member.get(memberID)
            def bookList = BorrowBook.createCriteria().list {
                and {
                    eq("member",member)
                    eq("isReturn",false)
                }

            }
            map.success = true
            map.bookList = bookList
            return map
        }else{
            map.message = "Invalid Request"
            return map
        }
    }




    def approveMemberByID(Integer id = 0){
        if (id != 0){
            def member = Member.get(id)
            member.isApprove = true
            member.save(flush: true)
            if (member.hasErrors()){
                return false
            }else{
                return true
            }
        }else{
            return false
        }
    }


    def getPendingMemberList(){
        def member = Member.createCriteria().list {
            eq("isApprove",false)
        }
        return member;
    }


}
