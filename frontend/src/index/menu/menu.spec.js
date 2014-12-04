/**
 * Created by User on 2014/7/23.
 */
describe('Index Menu', function () {
    var scope, ctrl, $httpBackend;

    beforeEach(module('Index.Menu'));

    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller) {
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/api/menu_items').
            respond([
                {id: '001', title: '用户', url: '/user', subMenu: ''},
                {id: '002', title: '任务单', url: '/task'},
                {id: '003', title: 'UI Elements', url: '/ui-elements', subMenu: [
                    {id: '003001', title: 'Elements', url: '/ui-elements/elements'},
                    {id: '003002', title: 'treeview', url: '/ui-elements/treeview'}
                ]},
                {id: '004', title: 'Tables', url: '/tables', subMenu: [
                    {id: '004001', title: 'Simple & Dynamic', url: '/tables/simple'},
                    {id: '004002', title: 'jqGrid plugin', url: '/tables/jqGrid_plugin'}
                ]},
                {id: '005', title: '日历', url: '/calendar'}
            ]);

        scope = $rootScope.$new();
        ctrl = $controller(menuCtrl, {$scope: scope});
    }));

//    it('should show menu list fetched from xhr', function () {
//        $httpBackend.flush();
//        expect(scope.menu).toEqual([
//            {id: '001', title: '用户', url: '/user', subMenu: ''},
//            {id: '002', title: '任务单', url: '/task'},
//            {id: '003', title: 'UI Elements', url: '/ui-elements', subMenu: [
//                {id: '003001', title: 'Elements', url: '/ui-elements/elements'},
//                {id: '003002', title: 'treeview', url: '/ui-elements/treeview'}
//            ]},
//            {id: '004', title: 'Tables', url: '/tables', subMenu: [
//                {id: '004001', title: 'Simple & Dynamic', url: '/tables/simple'},
//                {id: '004002', title: 'jqGrid plugin', url: '/tables/jqGrid_plugin'}
//            ]},
//            {id: '005', title: '日历', url: '/calendar'}
//        ]);
//    });

});