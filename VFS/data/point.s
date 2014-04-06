#class Point
#{
#    int X;
#    int Y;
#
#    int sum( )
#    {
#        return X + Y;
#    }
#
#}
#
#class Main
#{
#    void main()
#    {
#        P pref;
#
#        pref = new P();
#        pref.X = 1;
#        pref.Y = 5;
#
#        toString( pref );
#    }
#
#    void toString( Point pref )
#    {.
#         write( pref.sum() );
#    }
#}


        .section .rodata 
                .align 4

object_vtable:
        .long   0

main_vtable:
        .long   object_vtable
        .long   main_main
        .long   main_toString

point_vtable:
        .long   object_vtable
        .long   point_sum
        
.LC0:
        .string "hello world!\n"
        .text
        .globl  main
        .type   main,@function
    

main:
        pushl   %ebp
        movl    %esp, %ebp
        subl    $8, %esp
        andl    $-16, %esp

        call main_object           # instanciate Main-Object
        pushl   %eax                # push Main-Object on the stack
        call main_main
        addl    $4, %esp            # free arguments
                
        addl    $16, %esp
        movl    $0, %eax
        leave
        ret

main_main:       
                                    #set up
        pushl   %ebp
        movl    %esp, %ebp
                                    # locals
        subl    $4, %esp            # declare P p

                                    # body
                                    # P is now at -4(%ebp)
        call    point_object       # new P()            
        movl    %eax, -4(%ebp)      # store instantiated P
        movl    $1, 4(%eax)         # p.X = 1
        movl    $5, 8(%eax)         # p.Y = 5;

        movl    8(%ebp), %ecx
        pushl   %eax                # pass Point-Object reference
        pushl   %ecx                # pass Main-Object reference
        movl    (%ecx), %ecx        # loat vTable of Main
        call    *8(%ecx)
                                    # finish
        movl    $0, %eax            # return void
        leave
        ret

# method to instantiate the fields of a class 
main_object:
                                    # set up
        pushl   %ebp
        movl    %esp, %ebp
                                    # body
        pushl   $4                  # allocate space for fields
        call    malloc              # returns address in %eax
        addl    $4, %esp
        movl    $main_vtable, %ecx
        movl    %ecx, (%eax)
                                    # finish
        leave
        ret

# method toString of class Point, takes reference to object fields as first arguemnt 8(%ebp)
main_toString:
                                    # set up
        pushl   %ebp
        movl    %esp, %ebp
                                    # body
        movl    12(%ebp), %ecx      # first argument
        
        movl    (%ecx), %ecx        # load reference to vTable
        movl    12(%ebp), %edx
        pushl   %edx
        call    *4(%ecx)            # call sum()
        addl    $4, %esp
        pushl   %eax                # return value of sum      
        call    lecho
        addl    $4, %esp
                                    # finish
        leave
        ret
        
# method to instantiate the fields of a class 
point_object:
                                    # set up
        pushl   %ebp
        movl    %esp, %ebp
                                    # body
        pushl   $12                 # allocate space for X and Y
        call    malloc              # returns address in %eax
        addl    $4, %esp
        movl    $point_vtable, %ecx
        movl    %ecx, (%eax)
                                    # finish
        leave
        ret

# method sum of class Point, takes reference to object fields as first arguemnt 8(%ebp)
point_sum:
                                    # set up
        pushl   %ebp
        movl    %esp, %ebp
                                    # body
        movl    8(%ebp), %ecx       # load reference to fields
        movl    4(%ecx), %eax       # load Point::X
        movl    8(%ecx), %edx       # load Point::Y
        addl    %edx, %eax          # do computation, store in %eax
                                    # finish
        leave
        ret
        
lecho:
                                    # set up
        pushl   %ebp
        movl    %esp, %ebp

                                    # body
        pushl   $0x6425
        movl    %esp, %ebx
        pushl   8(%ebp)
        pushl   %ebx
        call    printf
        addl    $12, %esp

        movl    $0, %eax
                                    # finish
        leave
        ret

.Lfe1:
        .size   main,.Lfe1-main
        .ident	"GCC: (Gentoo 4.8.1-r1 p1.2, pie-0.5.7) 4.8.1"
        .section	.note.GNU-stack,"",@progbits
