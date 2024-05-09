import {All, Controller, Get, Param, Req, Res} from '@nestjs/common';
import { GatewayService } from './gateway.service';
import { Request, Response } from 'express';

@Controller()
export class GatewayController {
    constructor(private readonly gatewayService: GatewayService) {}

    @Get('auth/:path')
    authProxy(@Req() req: Request, @Res() res: Response, @Param('path') path?: string) {
        this.gatewayService.proxy(req, res, 'AUTH_SERVICE_URL', path);
    }

    @Get('vehicle/:path')
    vehicleProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'VEHICLE_SERVICE_URL', path);
    }

    @Get('order/:path')
    orderProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'ORDER_SERVICE_URL', path);
    }

    @Get('shipping/:path')
    shippingProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'SHIPPING_SERVICE_URL', path);
    }
}